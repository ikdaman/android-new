package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.model.SignupState
import project.side.domain.usecase.SignupUseCase
import project.side.domain.usecase.member.CheckNicknameUseCase
import project.side.presentation.model.SignupUIState
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val checkNicknameUseCase: CheckNicknameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUIState>(SignupUIState.Init)
    val uiState: StateFlow<SignupUIState> = _uiState.asStateFlow()

    fun checkNickname(nickname: String) {
        viewModelScope.launch {
            _uiState.value = SignupUIState.Loading
            checkNicknameUseCase(nickname).collect { result ->
                when (result) {
                    is DataResource.Loading -> _uiState.value = SignupUIState.Loading
                    is DataResource.Success -> _uiState.value = SignupUIState.NicknameChecked(result.data.available)
                    is DataResource.Error -> _uiState.value = SignupUIState.Error(result.message ?: "닉네임 확인 중 오류가 발생했습니다")
                }
            }
        }
    }

    fun signup(signupUseCase: SignupUseCase, socialToken: String, provider: String, providerId: String, nickname: String) {
        viewModelScope.launch {
            _uiState.value = SignupUIState.Loading
            signupUseCase(socialToken, provider, providerId, nickname).collect { state ->
                when (state) {
                    SignupState.Loading -> _uiState.value = SignupUIState.Loading
                    SignupState.Success -> _uiState.value = SignupUIState.Success
                    is SignupState.Error -> _uiState.value = SignupUIState.Error(state.message)
                }
            }
        }
    }
}
