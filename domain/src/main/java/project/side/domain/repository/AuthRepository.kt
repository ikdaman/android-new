package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.LoginState
import project.side.domain.model.LogoutState
import project.side.domain.model.SignupState

interface AuthRepository {
    fun googleLogin(): Flow<LoginState>
    fun naverLogin(): Flow<LoginState>
    fun kakaoLogin(): Flow<LoginState>

    fun googleLogout(): Flow<LogoutState>
    fun naverLogout(): Flow<LogoutState>
    fun kakaoLogout(): Flow<LogoutState>

    fun signup(socialToken: String?, provider: String?, providerId: String?, nickname: String?): Flow<SignupState>

    suspend fun getProvider(): String?
}