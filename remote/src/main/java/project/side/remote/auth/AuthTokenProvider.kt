package project.side.remote.auth

import project.side.data.auth.TokenCacheManager
import project.side.data.datasource.AuthDataStoreSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenProvider @Inject constructor(
    private val authDataStoreSource: AuthDataStoreSource
) : TokenCacheManager {
    @Volatile
    private var cachedToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

    fun getToken(): String? {
        return cachedToken
    }

    override fun getRefreshTokenSync(): String? {
        return cachedRefreshToken
    }

    override fun getAuthorizationSync(): String? {
        return cachedToken
    }

    override suspend fun updateToken() {
        val newToken = authDataStoreSource.getAuthorization()
        val newRefreshToken = authDataStoreSource.getRefreshToken()
        synchronized(this) {
            cachedToken = newToken
            cachedRefreshToken = newRefreshToken
        }
    }

    override suspend fun saveTokenAndUpdateCache(authorization: String, refreshToken: String) {
        authDataStoreSource.saveToken(
            authorization = authorization,
            refreshToken = refreshToken
        )
        synchronized(this) {
            cachedToken = authorization
            cachedRefreshToken = refreshToken
        }
    }

    override fun clearToken() {
        synchronized(this) {
            cachedToken = null
            cachedRefreshToken = null
        }
    }
}
