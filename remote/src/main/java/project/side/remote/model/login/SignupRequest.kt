package project.side.remote.model.login

data class SignupRequest(
    val provider: String?,
    val providerId: String?,
    val nickname: String?
)
