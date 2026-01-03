package project.side.remote.login

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import project.side.data.model.SocialLoginResult
import kotlin.coroutines.resume

object KakaoAuth {
    // SocialLoginResult.errorMessage -> 개발자 출력용
    suspend fun login(context: Context): SocialLoginResult =
        suspendCancellableCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, loginError ->
                when {
                    // 사용자가 로그인 취소(뒤로 가기 등)
                    loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled -> {
                        continuation.resume(
                            SocialLoginResult(
                                isSuccess = false,
                                errorMessage = ""
                            )
                        )
                    }

                    // 카카오 계정 로그인 실패
                    loginError != null || token == null -> {
                        continuation.resume(
                            SocialLoginResult(
                                isSuccess = false,
                                errorMessage = loginError?.message ?: "카카오 로그인에 실패했습니다."
                            )
                        )
                    }

                    // 카카오 계정 로그인 성공
                    else -> {
                        UserApiClient.instance.me { user, userInfoError ->
                            if (userInfoError != null || user?.id == null) {    // 사용자 정보 조회 실패
                                continuation.resume(
                                    SocialLoginResult(
                                        isSuccess = false,
                                        errorMessage = userInfoError?.message ?: "정보를 가져오는데 실패했습니다."
                                    )
                                )
                            } else {    // 사용자 정보 조회 성공(로그인, 정보 조회 둘 다 성공 시 소셜 로그인 성공)
                                continuation.resume(
                                    SocialLoginResult(
                                        isSuccess = true,
                                        socialAccessToken = token.accessToken,
                                        provider = "KAKAO",
                                        providerId = user.id.toString()
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                    if (loginError != null) {
                        // 사용자가 로그인 취소
                        if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                            continuation.resume(
                                SocialLoginResult(
                                    isSuccess = false,
                                    errorMessage = ""
                                )
                            )
                        }
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {     // 카카오톡으로 로그인 성공
                        UserApiClient.instance.me { user, userInfoError ->  // 사용자 정보 조회 실패
                            if (userInfoError != null || user?.id == null) {
                                continuation.resume(
                                    SocialLoginResult(
                                        isSuccess = false,
                                        errorMessage = userInfoError?.message ?: "정보를 가져오는데 실패했습니다."
                                    )
                                )
                            } else {    // 사용자 정보 조회 성공
                                continuation.resume(
                                    SocialLoginResult(
                                        isSuccess = true,
                                        socialAccessToken = token.accessToken,
                                        provider = "KAKAO",
                                        providerId = user.id.toString()
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }

    fun logout() {
        UserApiClient.instance.logout {}
    }

    suspend fun unlink(): Boolean = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.unlink { error ->
            continuation.resume(error == null)
        }
    }
}