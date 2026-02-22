package project.side.remote.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.model.AuthEvent
import project.side.data.model.DataAuthEvent
import project.side.remote.api.UserService
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authDataStoreSource: AuthDataStoreSource,
    private val authTokenProvider: AuthTokenProvider,
    private val userService: UserService
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") == null) {
            return null
        }

        if (response.code == 401) {
            synchronized(lock) {
                // 이미 다른 스레드가 토큰을 갱신했는지 확인
                val requestToken = response.request.header("Authorization")
                val currentToken = authTokenProvider.getToken()?.let { "Bearer $it" }
                if (currentToken != null && currentToken != requestToken) {
                    // 이미 갱신된 토큰이 있으므로 재시도만 수행
                    return response.request.newBuilder()
                        .header("Authorization", currentToken)
                        .build()
                }

                val refreshToken = runBlocking { authDataStoreSource.getRefreshToken() }
                val authorization = runBlocking { authDataStoreSource.getAuthorization() }
                if (refreshToken.isNullOrBlank() || authorization.isNullOrBlank()) {
                    clearToken()
                    return null
                }

                val result = try {
                    runBlocking { userService.reissue("Bearer $authorization", refreshToken) }
                } catch (e: Exception) {
                    clearToken()
                    return null
                }

                if (result.isSuccessful) {
                    val newAuthorization = result.headers()["Authorization"]
                    val newRefreshToken = result.headers()["refresh-token"]

                    if (newAuthorization != null && newRefreshToken != null) {
                        runBlocking {
                            authDataStoreSource.saveToken(
                                authorization = newAuthorization,
                                refreshToken = newRefreshToken
                            )
                            authTokenProvider.updateToken()
                        }
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $newAuthorization")
                            .build()
                    }
                }

                clearToken()
            }
        }
        return null
    }

    private fun clearToken() {
        runBlocking {
            authDataStoreSource.clear()
            authTokenProvider.clearToken()
        }
        runBlocking { AuthEvent.notify(DataAuthEvent.LOGIN_REQUIRED) }
    }
}
