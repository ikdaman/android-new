package project.side.remote.auth

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that adds Bearer token authentication to outgoing requests.
 * Uses a cached token provider to avoid blocking the network thread.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
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
