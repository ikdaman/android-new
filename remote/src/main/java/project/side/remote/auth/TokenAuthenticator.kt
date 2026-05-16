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
                val requestToken = response.request.header("Authorization")
                val currentToken = authTokenProvider.getToken()?.let { "Bearer $it" }
                if (currentToken != null && currentToken != requestToken) {
                    return response.request.newBuilder()
                        .header("Authorization", currentToken)
                        .build()
                }

                val refreshToken = authTokenProvider.getRefreshTokenSync()
                val authorization = authTokenProvider.getAuthorizationSync()
                if (refreshToken.isNullOrBlank() || authorization.isNullOrBlank()) {
                    clearToken()
                    return null
                }

                val result = try {
                    userService.reissueSync("Bearer $authorization", refreshToken).execute()
                } catch (e: Exception) {
                    // 네트워크 일시 오류: 토큰을 클리어하면 자동로그인이 깨짐.
                    // 그냥 retry 포기하되 토큰은 유지 → 다음 요청에서 다시 시도 가능.
                    return null
                }

                if (result.isSuccessful) {
                    val newAuthorization = result.headers()["Authorization"]
                    val newRefreshToken = result.headers()["refresh-token"]

                    if (newAuthorization != null && newRefreshToken != null) {
                        runBlocking {
                            authTokenProvider.saveTokenAndUpdateCache(
                                authorization = newAuthorization,
                                refreshToken = newRefreshToken
                            )
                        }
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $newAuthorization")
                            .build()
                    }
                    // 헤더 누락(서버 응답 이상) → 토큰 유지하고 retry 포기
                    return null
                }

                // reissue 가 401/403/404 로 명시적으로 거절했을 때만 강제 로그아웃.
                // 404 4040103 "Refresh Token이 유효하지 않습니다" 처럼 서버가 refresh token 자체를
                // 무효화한 경우도 여기에 포함. 5xx (서버 일시 오류) 는 토큰을 유지해 자동로그인 보존.
                if (shouldClearTokenOnReissueFailure(result.code())) {
                    clearToken()
                }
            }
        }
        return null
    }

    private fun clearToken() {
        runBlocking {
            authDataStoreSource.clear()
        }
        authTokenProvider.clearToken()
        runBlocking { AuthEvent.notify(DataAuthEvent.LOGIN_REQUIRED) }
    }

    internal companion object {
        /**
         * reissue 응답 코드가 토큰 자체의 무효를 의미하면 true.
         * - 401 / 403: 인증 실패
         * - 404 4040103: 서버가 refresh token 만료/무효 시 던지는 응답
         * - 5xx: 서버 일시 장애 가능성이 있어 토큰 유지 (자동로그인 보존)
         */
        internal fun shouldClearTokenOnReissueFailure(code: Int): Boolean =
            code == 401 || code == 403 || code == 404
    }
}
