package project.side.remote.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @POST("/auth/reissue")
    suspend fun reissue(
        @Header("Authorization") authorization: String,
        @Header("refresh-token") refreshToken: String
    ): Response<Unit>

    @POST("/auth/reissue")
    fun reissueSync(
        @Header("Authorization") authorization: String,
        @Header("refresh-token") refreshToken: String
    ): Call<Unit>
}