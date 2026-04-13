package project.side.data.auth

interface TokenCacheManager {
    suspend fun updateToken()
    fun clearToken()
    fun getRefreshTokenSync(): String?
    fun getAuthorizationSync(): String?
    suspend fun saveTokenAndUpdateCache(authorization: String, refreshToken: String)
}
