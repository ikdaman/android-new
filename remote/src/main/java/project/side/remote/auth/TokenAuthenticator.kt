package project.side.remote.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Authenticator that handles authentication failures (401 responses).
 * 
 * Currently, this authenticator returns null to prevent retry attempts since
 * there is no token refresh mechanism implemented. This is the correct behavior
 * to avoid infinite retry loops with the same token.
 * 
 * Future enhancement: Implement token refresh logic here when a refresh token
 * mechanism is available.
 */
@Singleton
class TokenAuthenticator @Inject constructor() : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't retry authentication failures for now since we don't have
        // a token refresh mechanism. This prevents infinite retry loops.
        // The user will need to log in again to get a fresh token.
        return null
    }
}
