package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.model.LogoutState
import project.side.domain.model.SocialAuthType
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import project.side.domain.usecase.member.UpdateNicknameUseCase
import project.side.presentation.model.SettingUIState
import project.side.presentation.util.SnackbarManager
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUIState>(SettingUIState.Init)
    val uiState: StateFlow<SettingUIState> = _uiState.asStateFlow()

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _isEditingNickname = MutableStateFlow(false)
    val isEditingNickname: StateFlow<Boolean> = _isEditingNickname.asStateFlow()

    private val _nicknameError = MutableStateFlow<String?>(null)
    val nicknameError: StateFlow<String?> = _nicknameError.asStateFlow()

    init {
        fetchNickname()
    }

    private fun fetchNickname() {
        viewModelScope.launch {
            getMyInfoUseCase().collect { result ->
                if (result is DataResource.Success) {
                    _nickname.value = result.data.nickname
                }
            }
        }
    }

    fun startEditingNickname() {
        _isEditingNickname.value = true
        _nicknameError.value = null
    }

    fun cancelEditingNickname() {
        _isEditingNickname.value = false
        _nicknameError.value = null
        fetchNickname()
    }

    fun validateNickname(input: String): Boolean {
        if (input.length > 10) {
            _nicknameError.value = "닉네임은 최대 10자까지 가능합니다"
            return false
        }
        val regex = Regex("^[가-힣a-zA-Z0-9]*$")
        if (!regex.matches(input)) {
            _nicknameError.value = "한글, 영어, 숫자만 사용할 수 있습니다"
            return false
        }
        _nicknameError.value = null
        return true
    }

    fun updateNickname(newNickname: String) {
        if (!validateNickname(newNickname)) return
        if (newNickname.isBlank()) {
            _nicknameError.value = "닉네임을 입력해주세요"
            return
        }

        viewModelScope.launch {
            updateNicknameUseCase(newNickname).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        _nickname.value = result.data.nickname
                        _isEditingNickname.value = false
                        _nicknameError.value = null
                        SnackbarManager.show("닉네임이 변경되었어요")
                    }
                    is DataResource.Error -> {
                        _nicknameError.value = result.message ?: "닉네임 변경에 실패했어요"
                    }
                    is DataResource.Loading -> {}
                }
            }
        }
    }

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
