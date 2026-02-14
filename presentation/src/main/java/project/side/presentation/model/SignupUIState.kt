package project.side.presentation.model

sealed class SignupUIState {
    data object Init : SignupUIState()
    data object Loading : SignupUIState()
    data object Success : SignupUIState()
    data object NicknameDuplicate : SignupUIState()
    data class NicknameChecked(val available: Boolean) : SignupUIState()
    data class Error(val message: String) : SignupUIState()
}
