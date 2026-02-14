package project.side.data.datasource

import project.side.data.model.DataApiResult
import project.side.data.model.LoginResult

interface AuthDataSource {
    suspend fun login(token: String, provider: String, providerId: String): DataApiResult<LoginResult>
    suspend fun logout(): DataApiResult<Unit>
    suspend fun signup(socialToken: String?, provider: String?, providerId: String?, nickname: String?): DataApiResult<LoginResult>
}