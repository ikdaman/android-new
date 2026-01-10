package project.side.data.datasource

import project.side.data.model.SocialLoginResult

interface SocialAuthDataSource {
    suspend fun googleLogin(): SocialLoginResult
    suspend fun naverLogin(): SocialLoginResult
    suspend fun kakaoLogin(): SocialLoginResult
    suspend fun googleLogout(): Boolean
    suspend fun naverLogout(): Boolean
    suspend fun kakaoLogout(): Boolean
}