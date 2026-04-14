package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.model.LoginState
import project.side.domain.usecase.auth.LoginUseCase
import project.side.presentation.model.AuthType
import project.side.presentation.model.LoginUIState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Init)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = LoginUIState.Init
    }

    fun googleLogin() = login(AuthType.GOOGLE)
    fun naverLogin() = login(AuthType.NAVER)
    fun kakaoLogin() = login(AuthType.KAKAO)

    private fun login(authType: AuthType) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading

            loginUseCase(authType.toDomainAuthType()).collect { loginState ->
                when (loginState) {
                    LoginState.Loading -> _uiState.value = LoginUIState.Loading
                    LoginState.Success -> _uiState.value = LoginUIState.Success("로그인 성공")
                    is LoginState.SignupRequired -> _uiState.value = LoginUIState.SignupRequired(
                        loginState.socialToken,
                        loginState.provider,
                        loginState.providerId
                    )
                    is LoginState.Error -> _uiState.value = LoginUIState.Error(loginState.message)
                }
            }
        }
    }
}