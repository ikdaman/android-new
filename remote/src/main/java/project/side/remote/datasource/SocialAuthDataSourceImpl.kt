package project.side.remote.datasource

import android.content.Context
import project.side.data.datasource.SocialAuthDataSource
import project.side.data.model.SocialLoginResult
import project.side.remote.login.GoogleAuth
import project.side.remote.login.KakaoAuth
import project.side.remote.login.NaverAuth
import javax.inject.Inject

class SocialAuthDataSourceImpl @Inject constructor(
    private val context: Context
): SocialAuthDataSource {
    override suspend fun googleLogin(): SocialLoginResult {
        return GoogleAuth.login(context)
    }

    override suspend fun naverLogin(): SocialLoginResult {
        return NaverAuth.login(context)
    }

    override suspend fun kakaoLogin(): SocialLoginResult {
        return KakaoAuth.login(context)
    }

    override suspend fun googleLogout(): Boolean {
        try {
            GoogleAuth.logout(context)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun naverLogout(): Boolean {
        try {
            NaverAuth.logout()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun kakaoLogout(): Boolean {
        try {
            KakaoAuth.logout()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}