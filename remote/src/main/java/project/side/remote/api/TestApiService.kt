package project.side.remote.api

import project.side.remote.model.NicknameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TestApiService {
    @GET("/members/check")
    suspend fun checkNickname(@Query("nickname") nickname : String): Response<NicknameResponse>
}