package project.side.data.datasource

import kotlinx.coroutines.flow.Flow

interface AuthDataStoreSource {
    suspend fun saveAuthInfo(
        provider: String,
        authorization: String,
        refreshToken: String,
        nickname: String
    )
    suspend fun clear()
    suspend fun getAuthorization(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveToken(
        authorization: String,
        refreshToken: String
    )
    fun isLoggedIn(): Flow<Boolean>
}