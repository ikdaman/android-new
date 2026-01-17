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
        return cachedToken ?: loadTokenSync()
    }

    /**
     * Loads the token synchronously from the data store.
     * This is only called when the cache is empty.
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
     */
    suspend fun updateToken() {
        cachedToken = authDataStoreSource.getAuthorization()
    }

    /**
     * Clears the cached token. Should be called on logout.
     */
    fun clearToken() {
        cachedToken = null
    }
}
