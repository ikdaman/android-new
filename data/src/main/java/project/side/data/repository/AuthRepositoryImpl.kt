package project.side.data.repository

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import project.side.data.datasource.AuthDataSource
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.SocialAuthDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.SocialLoginResult
import project.side.domain.model.LoginState
import project.side.domain.model.LogoutState
import project.side.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val socialAuthDataSource: SocialAuthDataSource,
    private val authDataStoreSource: AuthDataStoreSource
) : AuthRepository {
    override fun googleLogin() = flow {
        emit(LoginState.Loading)
        val socialResult = socialAuthDataSource.googleLogin()
        processLogin(socialResult)
    }

    override fun naverLogin() = flow {
        emit(LoginState.Loading)
        val socialResult = socialAuthDataSource.naverLogin()
        processLogin(socialResult)
    }

    override fun kakaoLogin() = flow {
        emit(LoginState.Loading)
        val socialResult = socialAuthDataSource.kakaoLogin()
        processLogin(socialResult)
    }

    override fun googleLogout() = flow {
        processLogout(socialAuthDataSource::googleLogout)
    }

    override fun naverLogout() = flow {
        processLogout(socialAuthDataSource::naverLogout)
    }

    override fun kakaoLogout() = flow {
        processLogout(socialAuthDataSource::kakaoLogout)
    }

    private suspend fun FlowCollector<LogoutState>.processLogout(logout: suspend () -> Boolean) {
        emit(LogoutState.Loading)
        val result = authDataSource.logout()
        if (result is DataApiResult.Success) {
            val socialResult = logout()
            if (socialResult) {
                authDataStoreSource.clear()
                emit(LogoutState.Success)
            } else {
                emit(LogoutState.Error("로그아웃 실패"))
            }
        } else {
            emit(LogoutState.Error("로그아웃 실패"))
        }
    }

    override fun signup(
        socialToken: String?,
        provider: String?,
        providerId: String?,
        nickname: String?
    ) = flow {
        emit(project.side.domain.model.SignupState.Loading)
        val signupResult = authDataSource.signup(socialToken, provider, providerId, nickname)
        if (signupResult is DataApiResult.Success) {
            val data = signupResult.data
            authDataStoreSource.saveAuthInfo(
                provider ?: "",
                data.authorization,
                data.refreshToken,
                data.nickname
            )
            emit(project.side.domain.model.SignupState.Success)
        } else if (signupResult is DataApiResult.Error) {
            emit(project.side.domain.model.SignupState.Error(signupResult.message))
        }
    }

    private suspend fun FlowCollector<LoginState>.processLogin(
        socialResult: SocialLoginResult
    ) {
        if (socialResult.isSuccess) {
            val provider = socialResult.provider
            val accessToken = socialResult.socialAccessToken
            val providerId = socialResult.providerId

            if (provider == null || accessToken == null || providerId == null) {
                emit(LoginState.Error("로그인 정보가 올바르지 않습니다."))
                return
            }

            val loginResult = authDataSource.login(
                accessToken,
                provider,
                providerId
            )
            if (loginResult is DataApiResult.Success) {
                val data = loginResult.data
                authDataStoreSource.saveAuthInfo(
                    provider,
                    data.authorization,
                    data.refreshToken,
                    data.nickname
                )
                emit(LoginState.Success)
            } else if (loginResult is DataApiResult.Error && loginResult.code == 404) {
                emit(LoginState.SignupRequired(accessToken, provider, providerId))
            } else {
                emit(LoginState.Error("로그인 실패"))
            }
        } else {
            emit(LoginState.Error(socialResult.errorMessage ?: "Unknown Error"))
        }
    }


}