package project.side.domain.model

sealed class LoginState {
    data object Loading : LoginState()
    data object Success : LoginState()
    data class SignupRequired(
        val socialToken: String,
        val provider: String,
        val providerId: String
    ) : LoginState()
    data class Error(val message: String) : LoginState()
}