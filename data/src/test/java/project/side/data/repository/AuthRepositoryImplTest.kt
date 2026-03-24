package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.AuthDataSource
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.SocialAuthDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.LoginResult
import project.side.data.model.SocialLoginResult
import project.side.domain.model.LoginState
import project.side.domain.model.LogoutState
import project.side.domain.model.SignupState

class AuthRepositoryImplTest {

    @MockK
    private lateinit var authDataSource: AuthDataSource

    @MockK
    private lateinit var socialAuthDataSource: SocialAuthDataSource

    @MockK
    private lateinit var authDataStoreSource: AuthDataStoreSource

    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AuthRepositoryImpl(authDataSource, socialAuthDataSource, authDataStoreSource)
    }

    // googleLogin tests
    @Test
    fun `googleLogin emits loading then success when social login and backend login succeed`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = true,
            socialAccessToken = "google_token",
            provider = "google",
            providerId = "google_id_123"
        )
        val loginResult = LoginResult(
            provider = "google",
            authorization = "auth_token",
            refreshToken = "refresh_token",
            nickname = "테스트유저"
        )
        coEvery { socialAuthDataSource.googleLogin() } returns socialResult
        coEvery { authDataSource.login("google_token", "google", "google_id_123") } returns DataApiResult.Success(loginResult)
        coEvery { authDataStoreSource.saveAuthInfo("google", "auth_token", "refresh_token", "테스트유저") } returns Unit

        // When
        val results = repository.googleLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Success)
        coVerify(exactly = 1) { socialAuthDataSource.googleLogin() }
        coVerify(exactly = 1) { authDataSource.login("google_token", "google", "google_id_123") }
        coVerify(exactly = 1) { authDataStoreSource.saveAuthInfo("google", "auth_token", "refresh_token", "테스트유저") }
    }

    @Test
    fun `googleLogin emits loading then signupRequired when backend returns 404`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = true,
            socialAccessToken = "google_token",
            provider = "google",
            providerId = "google_id_123"
        )
        coEvery { socialAuthDataSource.googleLogin() } returns socialResult
        coEvery { authDataSource.login("google_token", "google", "google_id_123") } returns DataApiResult.Error("Not Found", 404)

        // When
        val results = repository.googleLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.SignupRequired)
        val signupRequired = results[1] as LoginState.SignupRequired
        assertEquals("google_token", signupRequired.socialToken)
        assertEquals("google", signupRequired.provider)
        assertEquals("google_id_123", signupRequired.providerId)
    }

    @Test
    fun `googleLogin emits loading then error when backend login fails with non-404 error`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = true,
            socialAccessToken = "google_token",
            provider = "google",
            providerId = "google_id_123"
        )
        coEvery { socialAuthDataSource.googleLogin() } returns socialResult
        coEvery { authDataSource.login("google_token", "google", "google_id_123") } returns DataApiResult.Error("서버 오류", 500)

        // When
        val results = repository.googleLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Error)
    }

    @Test
    fun `googleLogin emits loading then error when social login fails`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = false,
            errorMessage = "소셜 로그인 실패"
        )
        coEvery { socialAuthDataSource.googleLogin() } returns socialResult

        // When
        val results = repository.googleLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Error)
        assertEquals("소셜 로그인 실패", (results[1] as LoginState.Error).message)
    }

    @Test
    fun `googleLogin emits loading then error with unknown error when social login fails with null message`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = false,
            errorMessage = null
        )
        coEvery { socialAuthDataSource.googleLogin() } returns socialResult

        // When
        val results = repository.googleLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Error)
        assertEquals("Unknown Error", (results[1] as LoginState.Error).message)
    }

    @Test
    fun `googleLogin emits loading then error when social login succeeds but provider is null`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = true,
            socialAccessToken = "token",
            provider = null,
            providerId = "id"
        )
        coEvery { socialAuthDataSource.googleLogin() } returns socialResult

        // When
        val results = repository.googleLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Error)
        assertEquals("로그인 정보가 올바르지 않습니다.", (results[1] as LoginState.Error).message)
    }

    // naverLogin tests
    @Test
    fun `naverLogin emits loading then success when social login and backend login succeed`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = true,
            socialAccessToken = "naver_token",
            provider = "naver",
            providerId = "naver_id_123"
        )
        val loginResult = LoginResult(
            provider = "naver",
            authorization = "auth_token",
            refreshToken = "refresh_token",
            nickname = "네이버유저"
        )
        coEvery { socialAuthDataSource.naverLogin() } returns socialResult
        coEvery { authDataSource.login("naver_token", "naver", "naver_id_123") } returns DataApiResult.Success(loginResult)
        coEvery { authDataStoreSource.saveAuthInfo("naver", "auth_token", "refresh_token", "네이버유저") } returns Unit

        // When
        val results = repository.naverLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Success)
        coVerify(exactly = 1) { socialAuthDataSource.naverLogin() }
    }

    @Test
    fun `naverLogin emits loading then error when social login fails`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = false,
            errorMessage = "네이버 로그인 실패"
        )
        coEvery { socialAuthDataSource.naverLogin() } returns socialResult

        // When
        val results = repository.naverLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Error)
        assertEquals("네이버 로그인 실패", (results[1] as LoginState.Error).message)
    }

    // kakaoLogin tests
    @Test
    fun `kakaoLogin emits loading then success when social login and backend login succeed`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = true,
            socialAccessToken = "kakao_token",
            provider = "kakao",
            providerId = "kakao_id_123"
        )
        val loginResult = LoginResult(
            provider = "kakao",
            authorization = "auth_token",
            refreshToken = "refresh_token",
            nickname = "카카오유저"
        )
        coEvery { socialAuthDataSource.kakaoLogin() } returns socialResult
        coEvery { authDataSource.login("kakao_token", "kakao", "kakao_id_123") } returns DataApiResult.Success(loginResult)
        coEvery { authDataStoreSource.saveAuthInfo("kakao", "auth_token", "refresh_token", "카카오유저") } returns Unit

        // When
        val results = repository.kakaoLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Success)
        coVerify(exactly = 1) { socialAuthDataSource.kakaoLogin() }
    }

    @Test
    fun `kakaoLogin emits loading then error when social login fails`() = runTest {
        // Given
        val socialResult = SocialLoginResult(
            isSuccess = false,
            errorMessage = "카카오 로그인 실패"
        )
        coEvery { socialAuthDataSource.kakaoLogin() } returns socialResult

        // When
        val results = repository.kakaoLogin().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LoginState.Loading)
        assertTrue(results[1] is LoginState.Error)
        assertEquals("카카오 로그인 실패", (results[1] as LoginState.Error).message)
    }

    // googleLogout tests
    @Test
    fun `googleLogout emits loading then success`() = runTest {
        // Given
        coEvery { authDataSource.logout() } returns DataApiResult.Success(Unit)
        coEvery { socialAuthDataSource.googleLogout() } returns true
        coEvery { authDataStoreSource.clear() } returns Unit

        // When
        val results = repository.googleLogout().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LogoutState.Loading)
        assertTrue(results[1] is LogoutState.Success)
        coVerify(exactly = 1) { authDataSource.logout() }
        coVerify(exactly = 1) { socialAuthDataSource.googleLogout() }
        coVerify(exactly = 1) { authDataStoreSource.clear() }
    }

    // naverLogout tests
    @Test
    fun `naverLogout emits loading then success`() = runTest {
        // Given
        coEvery { authDataSource.logout() } returns DataApiResult.Success(Unit)
        coEvery { socialAuthDataSource.naverLogout() } returns true
        coEvery { authDataStoreSource.clear() } returns Unit

        // When
        val results = repository.naverLogout().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LogoutState.Loading)
        assertTrue(results[1] is LogoutState.Success)
        coVerify(exactly = 1) { socialAuthDataSource.naverLogout() }
    }

    // kakaoLogout tests
    @Test
    fun `kakaoLogout emits loading then success`() = runTest {
        // Given
        coEvery { authDataSource.logout() } returns DataApiResult.Success(Unit)
        coEvery { socialAuthDataSource.kakaoLogout() } returns true
        coEvery { authDataStoreSource.clear() } returns Unit

        // When
        val results = repository.kakaoLogout().toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is LogoutState.Loading)
        assertTrue(results[1] is LogoutState.Success)
        coVerify(exactly = 1) { socialAuthDataSource.kakaoLogout() }
    }

    // signup tests
    @Test
    fun `signup emits loading then success when datasource returns success`() = runTest {
        // Given
        val loginResult = LoginResult(
            provider = "google",
            authorization = "auth_token",
            refreshToken = "refresh_token",
            nickname = "신규유저"
        )
        coEvery {
            authDataSource.signup("social_token", "google", "provider_id", "신규유저")
        } returns DataApiResult.Success(loginResult)
        coEvery {
            authDataStoreSource.saveAuthInfo("google", "auth_token", "refresh_token", "신규유저")
        } returns Unit

        // When
        val results = repository.signup("social_token", "google", "provider_id", "신규유저").toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is SignupState.Loading)
        assertTrue(results[1] is SignupState.Success)
        coVerify(exactly = 1) { authDataSource.signup("social_token", "google", "provider_id", "신규유저") }
        coVerify(exactly = 1) { authDataStoreSource.saveAuthInfo("google", "auth_token", "refresh_token", "신규유저") }
    }

    @Test
    fun `signup saves auth info with empty string when provider is null`() = runTest {
        // Given
        val loginResult = LoginResult(
            provider = "",
            authorization = "auth_token",
            refreshToken = "refresh_token",
            nickname = "신규유저"
        )
        coEvery {
            authDataSource.signup("social_token", null, "provider_id", "신규유저")
        } returns DataApiResult.Success(loginResult)
        coEvery {
            authDataStoreSource.saveAuthInfo("", "auth_token", "refresh_token", "신규유저")
        } returns Unit

        // When
        val results = repository.signup("social_token", null, "provider_id", "신규유저").toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is SignupState.Loading)
        assertTrue(results[1] is SignupState.Success)
        coVerify(exactly = 1) { authDataStoreSource.saveAuthInfo("", "auth_token", "refresh_token", "신규유저") }
    }

    @Test
    fun `signup emits loading then error when datasource returns error`() = runTest {
        // Given
        val errorMessage = "회원가입 실패"
        coEvery {
            authDataSource.signup(any(), any(), any(), any())
        } returns DataApiResult.Error(errorMessage)

        // When
        val results = repository.signup("token", "google", "id", "닉네임").toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is SignupState.Loading)
        assertTrue(results[1] is SignupState.Error)
        assertEquals(errorMessage, (results[1] as SignupState.Error).message)
    }

    // getProvider tests
    @Test
    fun `getProvider returns provider from authDataStoreSource`() = runTest {
        // Given
        coEvery { authDataStoreSource.getProvider() } returns "google"

        // When
        val result = repository.getProvider()

        // Then
        assertEquals("google", result)
        coVerify(exactly = 1) { authDataStoreSource.getProvider() }
    }

    @Test
    fun `getProvider returns null when authDataStoreSource returns null`() = runTest {
        // Given
        coEvery { authDataStoreSource.getProvider() } returns null

        // When
        val result = repository.getProvider()

        // Then
        assertEquals(null, result)
    }
}
