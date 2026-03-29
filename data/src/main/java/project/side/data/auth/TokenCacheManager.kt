package project.side.data.auth

interface TokenCacheManager {
    suspend fun updateToken()
    fun clearToken()
}
