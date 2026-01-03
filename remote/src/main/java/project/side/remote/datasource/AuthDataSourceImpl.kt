package project.side.remote.datasource

import project.side.data.datasource.AuthDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.LoginResult
import project.side.remote.api.AuthService
import project.side.remote.model.login.LoginRequest
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : AuthDataSource {
    override suspend fun login(
        token: String,
        provider: String,
        providerId: String
    ): DataApiResult<LoginResult> {
        return try {
            val response = authService.login(token, LoginRequest(provider, providerId))
            if (response.isSuccessful) {
                val header = response.headers()
                val authorization = header["Authorization"]
                val refreshToken = header["refresh-token"]

                response.body()?.let {
                    if (!authorization.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                        return DataApiResult.Success(
                            LoginResult(
                                provider,
                                authorization,
                                refreshToken,
                                it.nickname ?: ""
                            )
                        )
                    } else DataApiResult.Error("토큰이 비어있습니다.")
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error("서버 오류: ${response.code()}, ${response.message()}")
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun logout(): DataApiResult<Unit> {
        return try {
            val response = authService.logout()
            if (response.isSuccessful) {
                DataApiResult.Success(Unit)
            } else DataApiResult.Error("서버 오류: ${response.code()}, ${response.message()}")
        } catch (e: Exception) {
            DataApiResult.Error("서버 오류: ${e.message}")
        }
    }
}