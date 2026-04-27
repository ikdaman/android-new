package project.side.remote.datasource

import project.side.data.datasource.SocialAuthDataSource
import project.side.data.model.SocialLoginResult
import project.side.remote.login.CurrentActivityHolder
import project.side.remote.login.GoogleAuth
import project.side.remote.login.KakaoAuth
import project.side.remote.login.NaverAuth
import javax.inject.Inject

class SocialAuthDataSourceImpl @Inject constructor(
    private val activityHolder: CurrentActivityHolder
): SocialAuthDataSource {
    override suspend fun googleLogin(): SocialLoginResult {
        return GoogleAuth.login(activityHolder.require())
    }

    override suspend fun naverLogin(): SocialLoginResult {
        return NaverAuth.login(activityHolder.require())
    }

    override suspend fun kakaoLogin(): SocialLoginResult {
        return KakaoAuth.login(activityHolder.require())
    }

    override suspend fun googleLogout(): Boolean {
        try {
            GoogleAuth.logout(activityHolder.require())
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun naverLogout(): Boolean {
        try {
            return NaverAuth.unlink()
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun kakaoLogout(): Boolean {
        try {
            return KakaoAuth.unlink()
        } catch (e: Exception) {
            return false
        }
    }
}
