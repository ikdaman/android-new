package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.LoginState
import project.side.domain.model.LogoutState

interface AuthRepository {
    fun googleLogin(): Flow<LoginState>
    fun naverLogin(): Flow<LoginState>
    fun kakaoLogin(): Flow<LoginState>

    fun googleLogout(): Flow<LogoutState>
    fun naverLogout(): Flow<LogoutState>
    fun kakaoLogout(): Flow<LogoutState>
}