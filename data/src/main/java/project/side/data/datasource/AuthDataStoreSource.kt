package project.side.data.datasource

interface AuthDataStoreSource {
    suspend fun saveAuthInfo(
        provider: String,
        authorization: String,
        refreshToken: String,
        nickname: String
    )
    suspend fun clear()
}