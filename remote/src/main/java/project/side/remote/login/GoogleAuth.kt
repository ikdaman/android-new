package project.side.remote.login

import android.content.Context
import android.util.Base64
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import project.side.remote.BuildConfig
import project.side.data.model.SocialLoginResult
import kotlin.coroutines.resume

object GoogleAuth {
    private val signInWithGoogleOption: GetSignInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_CLIENT_ID).build()

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    suspend fun login(context: Context): SocialLoginResult =
        suspendCancellableCoroutine { continuation ->
            val credentialManager = CredentialManager.create(context)

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    continuation.resume(handleSignIn(result))
                } catch (e: GetCredentialException) {
                    continuation.resume(
                        SocialLoginResult(
                            isSuccess = false,
                            errorMessage = e.message
                        )
                    )
                }
            }
        }

    suspend fun logout(context: Context) {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    private fun handleSignIn(result: GetCredentialResponse): SocialLoginResult {
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val providerId = getProviderId(googleIdTokenCredential.idToken)

                SocialLoginResult(
                    isSuccess = true,
                    socialAccessToken = googleIdTokenCredential.idToken,
                    provider = "GOOGLE",
                    providerId = providerId
                )
            } catch (e: GoogleIdTokenParsingException) {
                SocialLoginResult(
                    isSuccess = false,
                    errorMessage = "Received an invalid google id token response $e"
                )
            }
        }

        return SocialLoginResult(
            isSuccess = false,
            errorMessage = "Unexpected type of credential"
        )
    }

    private fun getProviderId(idToken: String): String {
        val payload = idToken.split(".")[1]
        val json = String(Base64.decode(payload, Base64.URL_SAFE), Charsets.UTF_8)
        return JSONObject(json).getString("sub")
    }
}
