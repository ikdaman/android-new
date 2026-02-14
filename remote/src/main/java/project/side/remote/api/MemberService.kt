package project.side.remote.api

import project.side.remote.model.member.MemberResponse
import project.side.remote.model.member.NicknameCheckResponse
import project.side.remote.model.member.NicknameUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface MemberService {
    @GET("/members/me")
    suspend fun getMyInfo(): Response<MemberResponse>

    @PATCH("/members/me")
    suspend fun updateNickname(@Body request: NicknameUpdateRequest): Response<MemberResponse>

    @DELETE("/members/me")
    suspend fun withdraw(): Response<Unit>

    @GET("/members/check")
    suspend fun checkNickname(@Query("nickname") nickname: String): Response<NicknameCheckResponse>
}
