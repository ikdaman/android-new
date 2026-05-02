package project.side.remote.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bearer token 을 모든 요청에 부착한다.
 *
 * 인메모리 캐시(`AuthTokenProvider.cachedToken`)를 우선 사용하되,
 * 앱 재시작 직후처럼 캐시가 비어있는 경우 **DataStore 에서 lazy 로 hydrate** 한다.
 * 이게 자동로그인의 핵심 — 캐시가 비었다고 무인증 요청을 그대로 보내면 서버가
 * 토큰 없는 요청으로 받아 5xx 를 던지고, 사용자에겐 "자동로그인 안 됨"으로 보임.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (authTokenProvider.getToken() == null) {
            // DataStore 에 토큰이 있으면 캐시로 끌어옴. 없으면 no-op.
            runBlocking { authTokenProvider.updateToken() }
        }

        val token = authTokenProvider.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
