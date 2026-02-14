package project.side.presentation.model


sealed class LoginUIState {
    data object Init : LoginUIState()
    data object Loading : LoginUIState()
    data class Success(val message: String) : LoginUIState()
    data class SignupRequired(
        val socialToken: String,
        val provider: String,
        val providerId: String
    ) : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}