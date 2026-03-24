package project.side.domain.usecase.auth

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.domain.model.LoginState
import project.side.domain.model.SocialAuthType
import project.side.domain.repository.AuthRepository

class LoginUseCaseTest {
    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: LoginUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = LoginUseCase(authRepository)
    }

    @Test
    fun `GOOGLE 로그인 성공 시 googleLogin flow를 반환한다`() = runTest {
        // Given
        val expectedFlow = flowOf(LoginState.Loading, LoginState.Success)
        every { authRepository.googleLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.GOOGLE).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LoginState.Loading)
        assert(results[1] is LoginState.Success)
        verify(exactly = 1) { authRepository.googleLogin() }
    }

    @Test
    fun `GOOGLE 로그인 실패 시 error flow를 반환한다`() = runTest {
        // Given
        val errorMessage = "Google login failed"
        val expectedFlow = flowOf(LoginState.Loading, LoginState.Error(errorMessage))
        every { authRepository.googleLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.GOOGLE).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LoginState.Loading)
        assert(results[1] is LoginState.Error)
        assertEquals(errorMessage, (results[1] as LoginState.Error).message)
        verify(exactly = 1) { authRepository.googleLogin() }
    }

    @Test
    fun `NAVER 로그인 성공 시 naverLogin flow를 반환한다`() = runTest {
        // Given
        val expectedFlow = flowOf(LoginState.Loading, LoginState.Success)
        every { authRepository.naverLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.NAVER).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LoginState.Loading)
        assert(results[1] is LoginState.Success)
        verify(exactly = 1) { authRepository.naverLogin() }
    }

    @Test
    fun `NAVER 로그인 실패 시 error flow를 반환한다`() = runTest {
        // Given
        val errorMessage = "Naver login failed"
        val expectedFlow = flowOf(LoginState.Loading, LoginState.Error(errorMessage))
        every { authRepository.naverLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.NAVER).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is LoginState.Error)
        assertEquals(errorMessage, (results[1] as LoginState.Error).message)
        verify(exactly = 1) { authRepository.naverLogin() }
    }

    @Test
    fun `KAKAO 로그인 성공 시 kakaoLogin flow를 반환한다`() = runTest {
        // Given
        val expectedFlow = flowOf(LoginState.Loading, LoginState.Success)
        every { authRepository.kakaoLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.KAKAO).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LoginState.Loading)
        assert(results[1] is LoginState.Success)
        verify(exactly = 1) { authRepository.kakaoLogin() }
    }

    @Test
    fun `KAKAO 로그인 실패 시 error flow를 반환한다`() = runTest {
        // Given
        val errorMessage = "Kakao login failed"
        val expectedFlow = flowOf(LoginState.Loading, LoginState.Error(errorMessage))
        every { authRepository.kakaoLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.KAKAO).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is LoginState.Error)
        assertEquals(errorMessage, (results[1] as LoginState.Error).message)
        verify(exactly = 1) { authRepository.kakaoLogin() }
    }

    @Test
    fun `GOOGLE 로그인 시 회원가입 필요 상태를 반환한다`() = runTest {
        // Given
        val signupRequired = LoginState.SignupRequired(
            socialToken = "token123",
            provider = "google",
            providerId = "id123"
        )
        val expectedFlow = flowOf(LoginState.Loading, signupRequired)
        every { authRepository.googleLogin() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.GOOGLE).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is LoginState.SignupRequired)
        val result = results[1] as LoginState.SignupRequired
        assertEquals("token123", result.socialToken)
        assertEquals("google", result.provider)
        verify(exactly = 1) { authRepository.googleLogin() }
    }

    @Test
    fun `각 소셜 타입은 대응하는 repository 메서드만 호출한다`() = runTest {
        // Given
        every { authRepository.googleLogin() } returns flowOf(LoginState.Success)
        every { authRepository.naverLogin() } returns flowOf(LoginState.Success)
        every { authRepository.kakaoLogin() } returns flowOf(LoginState.Success)

        // When
        useCase.invoke(SocialAuthType.GOOGLE).toList()

        // Then
        verify(exactly = 1) { authRepository.googleLogin() }
        verify(exactly = 0) { authRepository.naverLogin() }
        verify(exactly = 0) { authRepository.kakaoLogin() }
    }
}
