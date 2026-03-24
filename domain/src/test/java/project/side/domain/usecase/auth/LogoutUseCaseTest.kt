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
import project.side.domain.model.LogoutState
import project.side.domain.model.SocialAuthType
import project.side.domain.repository.AuthRepository

class LogoutUseCaseTest {
    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: LogoutUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = LogoutUseCase(authRepository)
    }

    @Test
    fun `GOOGLE 로그아웃 성공 시 googleLogout flow를 반환한다`() = runTest {
        // Given
        val expectedFlow = flowOf(LogoutState.Loading, LogoutState.Success)
        every { authRepository.googleLogout() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.GOOGLE).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LogoutState.Loading)
        assert(results[1] is LogoutState.Success)
        verify(exactly = 1) { authRepository.googleLogout() }
    }

    @Test
    fun `GOOGLE 로그아웃 실패 시 error flow를 반환한다`() = runTest {
        // Given
        val errorMessage = "Google logout failed"
        val expectedFlow = flowOf(LogoutState.Loading, LogoutState.Error(errorMessage))
        every { authRepository.googleLogout() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.GOOGLE).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is LogoutState.Error)
        assertEquals(errorMessage, (results[1] as LogoutState.Error).message)
        verify(exactly = 1) { authRepository.googleLogout() }
    }

    @Test
    fun `NAVER 로그아웃 성공 시 naverLogout flow를 반환한다`() = runTest {
        // Given
        val expectedFlow = flowOf(LogoutState.Loading, LogoutState.Success)
        every { authRepository.naverLogout() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.NAVER).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LogoutState.Loading)
        assert(results[1] is LogoutState.Success)
        verify(exactly = 1) { authRepository.naverLogout() }
    }

    @Test
    fun `NAVER 로그아웃 실패 시 error flow를 반환한다`() = runTest {
        // Given
        val errorMessage = "Naver logout failed"
        val expectedFlow = flowOf(LogoutState.Loading, LogoutState.Error(errorMessage))
        every { authRepository.naverLogout() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.NAVER).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is LogoutState.Error)
        assertEquals(errorMessage, (results[1] as LogoutState.Error).message)
        verify(exactly = 1) { authRepository.naverLogout() }
    }

    @Test
    fun `KAKAO 로그아웃 성공 시 kakaoLogout flow를 반환한다`() = runTest {
        // Given
        val expectedFlow = flowOf(LogoutState.Loading, LogoutState.Success)
        every { authRepository.kakaoLogout() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.KAKAO).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is LogoutState.Loading)
        assert(results[1] is LogoutState.Success)
        verify(exactly = 1) { authRepository.kakaoLogout() }
    }

    @Test
    fun `KAKAO 로그아웃 실패 시 error flow를 반환한다`() = runTest {
        // Given
        val errorMessage = "Kakao logout failed"
        val expectedFlow = flowOf(LogoutState.Loading, LogoutState.Error(errorMessage))
        every { authRepository.kakaoLogout() } returns expectedFlow

        // When
        val results = useCase.invoke(SocialAuthType.KAKAO).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is LogoutState.Error)
        assertEquals(errorMessage, (results[1] as LogoutState.Error).message)
        verify(exactly = 1) { authRepository.kakaoLogout() }
    }

    @Test
    fun `각 소셜 타입은 대응하는 repository 메서드만 호출한다`() = runTest {
        // Given
        every { authRepository.googleLogout() } returns flowOf(LogoutState.Success)
        every { authRepository.naverLogout() } returns flowOf(LogoutState.Success)
        every { authRepository.kakaoLogout() } returns flowOf(LogoutState.Success)

        // When
        useCase.invoke(SocialAuthType.KAKAO).toList()

        // Then
        verify(exactly = 1) { authRepository.kakaoLogout() }
        verify(exactly = 0) { authRepository.googleLogout() }
        verify(exactly = 0) { authRepository.naverLogout() }
    }
}
