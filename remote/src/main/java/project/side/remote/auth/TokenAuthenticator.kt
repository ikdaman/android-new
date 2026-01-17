package project.side.remote.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Authenticator that adds Bearer token authentication to requests.
 * This is the recommended approach for handling authentication in OkHttp,
 * as opposed to using interceptors which can block network threads.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // If this is already a retry with the same token, don't retry again
        val currentToken = authTokenProvider.getToken()
        
        if (response.request.header("Authorization") == "Bearer $currentToken") {
            // Token is the same as before, no point in retrying
            return null
        }

        // Get fresh token and retry
        val token = currentToken ?: return null
        
        return response.request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}
