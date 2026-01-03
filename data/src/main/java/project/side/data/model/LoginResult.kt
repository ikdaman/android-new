package project.side.data.model

data class LoginResult(
    val provider: String,
    val authorization: String,
    val refreshToken: String,
    val nickname: String
)
