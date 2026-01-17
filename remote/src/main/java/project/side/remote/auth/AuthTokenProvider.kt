package project.side.remote.auth

import kotlinx.coroutines.runBlocking
import project.side.data.datasource.AuthDataStoreSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe token provider that maintains a cached authorization token.
 * This allows synchronous access to the token for OkHttp interceptors/authenticators
 * while still supporting async token updates.
 */
@Singleton
class AuthTokenProvider @Inject constructor(
    private val authDataStoreSource: AuthDataStoreSource
) {
    @Volatile
    private var cachedToken: String? = null

    /**
     * Gets the current authorization token synchronously.
     * If no token is cached, it will be loaded from the data store.
     * This method is thread-safe and can be called from OkHttp's network threads.
     */
    fun getToken(): String? {
        // Double-checked locking to prevent race condition
        cachedToken?.let { return it }
        
        synchronized(this) {
            cachedToken?.let { return it }
            return loadTokenSync()
        }
    }

    /**
     * Loads the token synchronously from the data store.
     * This is only called when the cache is empty and should only be called
     * from within a synchronized block.
     */
    private fun loadTokenSync(): String? {
        return runBlocking {
            authDataStoreSource.getAuthorization().also {
                cachedToken = it
            }
        }
    }

    /**
     * Updates the cached token. Should be called after login or token refresh.
     * 
     * Integration guidance:
     * - Call this method in your AuthRepository after successfully saving auth info
     * - This ensures the network layer has the latest token without needing to reload
     * - Example: After authDataStoreSource.saveAuthInfo(), call authTokenProvider.updateToken()
     * 
     * Note: This method uses runBlocking to maintain consistent synchronization with getToken().
     * The blocking is acceptable here since token updates happen infrequently (only on login/refresh).
     */
    suspend fun updateToken() {
        val newToken = authDataStoreSource.getAuthorization()
        synchronized(this) {
            cachedToken = newToken
        }
    }

    /**
     * Clears the cached token. Should be called on logout.
     * 
     * Integration guidance:
     * - Call this method when the user logs out
     * - This ensures subsequent requests won't use a stale token
     * - Example: After authDataStoreSource.clear(), call authTokenProvider.clearToken()
     */
    fun clearToken() {
        synchronized(this) {
            cachedToken = null
        }
    }
}
