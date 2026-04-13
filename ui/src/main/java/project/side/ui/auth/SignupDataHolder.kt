package project.side.ui.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignupDataHolder @Inject constructor() {
    private var socialToken: String? = null
    private var provider: String? = null
    private var providerId: String? = null

    fun set(socialToken: String, provider: String, providerId: String) {
        this.socialToken = socialToken
        this.provider = provider
        this.providerId = providerId
    }

    fun consume(): SignupData? {
        val token = socialToken ?: return null
        val prov = provider ?: return null
        val provId = providerId ?: return null
        clear()
        return SignupData(token, prov, provId)
    }

    private fun clear() {
        socialToken = null
        provider = null
        providerId = null
    }
}

data class SignupData(
    val socialToken: String,
    val provider: String,
    val providerId: String
)
