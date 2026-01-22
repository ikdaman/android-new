package project.side.remote.api

import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @POST("auth/reissue")
    suspend fun reissue(
        @Header("refresh-token") refreshToken: String
    ): Response<Unit>
}