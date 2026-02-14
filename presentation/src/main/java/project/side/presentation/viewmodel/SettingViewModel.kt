package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.model.LogoutState
import project.side.domain.model.SocialAuthType
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.model.SettingUIState

class SettingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUIState>(SettingUIState.Init)
    val uiState: StateFlow<SettingUIState> = _uiState.asStateFlow()

    fun logout(logoutUseCase: LogoutUseCase, getProviderUseCase: GetProviderUseCase) {
        viewModelScope.launch {
            _uiState.value = SettingUIState.Loading
            val provider = getProviderUseCase()
            val authType = when (provider?.uppercase()) {
                "GOOGLE" -> SocialAuthType.GOOGLE
                "NAVER" -> SocialAuthType.NAVER
                "KAKAO" -> SocialAuthType.KAKAO
                else -> {
                    _uiState.value = SettingUIState.Error("로그인 정보를 찾을 수 없습니다.")
                    return@launch
                }
            }
            logoutUseCase(authType).collect { logoutState ->
                when (logoutState) {
                    LogoutState.Loading -> _uiState.value = SettingUIState.Loading
                    LogoutState.Success -> _uiState.value = SettingUIState.LogoutSuccess
                    is LogoutState.Error -> _uiState.value = SettingUIState.Error(logoutState.message)
                }
            }
        }
    }
}
