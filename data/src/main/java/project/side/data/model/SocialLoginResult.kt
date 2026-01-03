package project.side.data.model

data class SocialLoginResult(
    val isSuccess: Boolean,
    val socialAccessToken: String? = null,
    val provider: String? = null,
    val providerId: String? = null,
    val errorMessage: String? = null
)