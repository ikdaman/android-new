package project.side.remote.api

import project.side.remote.model.login.LoginRequest
import project.side.remote.model.login.LoginResponse
import project.side.remote.model.login.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/login")
    suspend fun login(
        @Header("social-token") accessToken: String?,
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @DELETE("/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("/auth/signup")
    suspend fun signup(
        @Header("social-token") socialToken: String?,
        @Body signupRequest: SignupRequest
    ): Response<LoginResponse>
}
