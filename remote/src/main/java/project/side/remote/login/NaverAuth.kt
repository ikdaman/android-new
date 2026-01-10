package project.side.remote.login

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import project.side.data.model.SocialLoginResult
import kotlin.coroutines.resume

object NaverAuth {
    suspend fun login(context: Context): SocialLoginResult {
        val (accessToken, loginError) = getAccessToken(context)
        val (providerId, userInfoError) = getProviderId()

        if (accessToken.isNullOrEmpty()) {
            return SocialLoginResult(
                isSuccess = false,
                errorMessage = loginError ?: "네이버 로그인에 실패했습니다."
            )
        }
        if (providerId.isNullOrEmpty()) {
            return SocialLoginResult(
                isSuccess = false,
                errorMessage = userInfoError ?: "정보를 가져오는데 실패했습니다."
            )
        }

        return SocialLoginResult(
            isSuccess = true,
            socialAccessToken = accessToken,
            provider = "NAVER",
            providerId = providerId
        )
    }

    fun logout() {
        NaverIdLoginSDK.logout()
    }

    suspend fun unlink() = suspendCancellableCoroutine { continuation ->
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() {
                continuation.resume(true)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resume(false)
            }

            override fun onError(errorCode: Int, message: String) {
                continuation.resume(false)
            }
        })
    }
}

private suspend fun getAccessToken(context: Context): Pair<String?, String?> =
    suspendCancellableCoroutine { continuation ->
        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                continuation.resume(NaverIdLoginSDK.getAccessToken() to null)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resume(null to "httpStatus: $httpStatus, $message")
            }

            override fun onError(errorCode: Int, message: String) {
                continuation.resume(null to "errorCode: $errorCode, $message")
            }
        })
    }

private suspend fun getProviderId(): Pair<String?, String?> =
    suspendCancellableCoroutine { continuation ->
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                continuation.resume(result.profile?.id to null)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resume(null to "httpStatus: $httpStatus, $message")
            }

            override fun onError(errorCode: Int, message: String) {
                continuation.resume(null to "errorCode: $errorCode, $message")
            }
        })
    }

