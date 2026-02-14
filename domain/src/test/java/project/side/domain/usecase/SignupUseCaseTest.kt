package project.side.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.domain.model.SignupState
import project.side.domain.repository.AuthRepository

class SignupUseCaseTest {
    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: SignupUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SignupUseCase(authRepository)
    }

    @Test
    fun `invoke returns success flow from repository`() = runTest {
        // Given
        val socialToken = "test_token"
        val provider = "kakao"
        val providerId = "12345"
        val nickname = "testuser"
        val expectedFlow = flowOf(SignupState.Loading, SignupState.Success)
        coEvery {
            authRepository.signup(socialToken, provider, providerId, nickname)
        } returns expectedFlow

        // When
        val results = useCase.invoke(socialToken, provider, providerId, nickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is SignupState.Loading)
        assert(results[1] is SignupState.Success)
        coVerify(exactly = 1) {
            authRepository.signup(socialToken, provider, providerId, nickname)
        }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val socialToken = "test_token"
        val provider = "kakao"
        val providerId = "12345"
        val nickname = "testuser"
        val errorMessage = "Signup failed"
        val expectedFlow = flowOf(SignupState.Loading, SignupState.Error(errorMessage))
        coEvery {
            authRepository.signup(socialToken, provider, providerId, nickname)
        } returns expectedFlow

        // When
        val results = useCase.invoke(socialToken, provider, providerId, nickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is SignupState.Loading)
        assert(results[1] is SignupState.Error)
        assertEquals(errorMessage, (results[1] as SignupState.Error).message)
        coVerify(exactly = 1) {
            authRepository.signup(socialToken, provider, providerId, nickname)
        }
    }

    @Test
    fun `invoke passes correct parameters to repository`() = runTest {
        // Given
        val socialToken = "custom_token"
        val provider = "google"
        val providerId = "67890"
        val nickname = "customuser"
        val expectedFlow = flowOf(SignupState.Loading, SignupState.Success)
        coEvery {
            authRepository.signup(socialToken, provider, providerId, nickname)
        } returns expectedFlow

        // When
        useCase.invoke(socialToken, provider, providerId, nickname).toList()

        // Then
        coVerify(exactly = 1) {
            authRepository.signup(socialToken, provider, providerId, nickname)
        }
    }
}
