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
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Init)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun googleLogin(loginUseCase: LoginUseCase) = login(AuthType.GOOGLE, loginUseCase)
    fun naverLogin(loginUseCase: LoginUseCase) = login(AuthType.NAVER, loginUseCase)
    fun kakaoLogin(loginUseCase: LoginUseCase) = login(AuthType.KAKAO, loginUseCase)

    fun googleLogout(logoutUseCase: LogoutUseCase) = logout(AuthType.GOOGLE, logoutUseCase)
    fun naverLogout(logoutUseCase: LogoutUseCase) = logout(AuthType.NAVER, logoutUseCase)
    fun kakaoLogout(logoutUseCase: LogoutUseCase) = logout(AuthType.KAKAO, logoutUseCase)


    private fun logout(authType: AuthType, logoutUseCase: LogoutUseCase) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            logoutUseCase(authType.toDomainAuthType()).collect { logoutState ->
                when (logoutState) {
                    LoginState.Loading -> _uiState.value = UIState.Loading
                    LoginState.Success -> _uiState.value = UIState.Success("로그아웃 성공")
                    else -> {}
                }
            }
        }
    }


    private fun login(authType: AuthType, loginUseCase: LoginUseCase) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            loginUseCase(authType.toDomainAuthType()).collect { loginState ->
                when (loginState) {
                    LoginState.Loading -> _uiState.value = UIState.Loading
                    LoginState.Success -> _uiState.value = UIState.Success("로그인 성공")
                    is LoginState.Error -> _uiState.value = UIState.Error(loginState.message)
                }
            }
        }
    }

    sealed class UIState {
        data object Init : UIState()
        data object Loading : UIState()
        data class Success(val message: String) : UIState()
        data class Error(val message: String) : UIState()
    }

    enum class AuthType {
        GOOGLE, NAVER, KAKAO;

        fun toDomainAuthType(): SocialAuthType {
            return when (this) {
                GOOGLE -> SocialAuthType.GOOGLE
                NAVER -> SocialAuthType.NAVER
                KAKAO -> SocialAuthType.KAKAO
            }
        }
    }
}