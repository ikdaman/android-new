package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.model.LoginState
import project.side.domain.model.SocialAuthType
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.model.AuthType
import project.side.presentation.model.LoginUIState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Init)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun googleLogin(loginUseCase: LoginUseCase) = login(AuthType.GOOGLE, loginUseCase)
    fun naverLogin(loginUseCase: LoginUseCase) = login(AuthType.NAVER, loginUseCase)
    fun kakaoLogin(loginUseCase: LoginUseCase) = login(AuthType.KAKAO, loginUseCase)

    fun googleLogout(logoutUseCase: LogoutUseCase) = logout(AuthType.GOOGLE, logoutUseCase)
    fun naverLogout(logoutUseCase: LogoutUseCase) = logout(AuthType.NAVER, logoutUseCase)
    fun kakaoLogout(logoutUseCase: LogoutUseCase) = logout(AuthType.KAKAO, logoutUseCase)


    private fun logout(authType: AuthType, logoutUseCase: LogoutUseCase) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading

            logoutUseCase(authType.toDomainAuthType()).collect { logoutState ->
                when (logoutState) {
                    LoginState.Loading -> _uiState.value = LoginUIState.Loading
                    LoginState.Success -> _uiState.value = LoginUIState.Success("로그아웃 성공")
                    is LoginState.Error -> _uiState.value = LoginUIState.Error(logoutState.message)
                }
            }
        }
    }


    private fun login(authType: AuthType, loginUseCase: LoginUseCase) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading

            loginUseCase(authType.toDomainAuthType()).collect { loginState ->
                when (loginState) {
                    LoginState.Loading -> _uiState.value = LoginUIState.Loading
                    LoginState.Success -> _uiState.value = LoginUIState.Success("로그인 성공")
                    is LoginState.Error -> _uiState.value = LoginUIState.Error(loginState.message)
                }
            }
        }
    }
}