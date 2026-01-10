package project.side.remote.model.login

data class LoginRequest(
    val provider: String?,
    val providerId: String?
)

data class LoginResponse(
    val nickname: String?
)