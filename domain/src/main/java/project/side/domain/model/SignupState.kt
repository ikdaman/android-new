package project.side.domain.model

sealed class SignupState {
    data object Loading : SignupState()
    data object Success : SignupState()
    data class Error(val message: String) : SignupState()
}
