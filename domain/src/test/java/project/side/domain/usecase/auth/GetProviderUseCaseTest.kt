package project.side.domain.usecase.auth

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import project.side.domain.repository.AuthRepository

class GetProviderUseCaseTest {
    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var getProviderUseCase: GetProviderUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getProviderUseCase = GetProviderUseCase(authRepository)
    }

    @Test
    fun `invoke returns provider when available`() = runTest {
        // Given
        coEvery { authRepository.getProvider() } returns "GOOGLE"

        // When
        val result = getProviderUseCase()

        // Then
        assertEquals("GOOGLE", result)
        coVerify(exactly = 1) { authRepository.getProvider() }
    }

    @Test
    fun `invoke returns null when no provider`() = runTest {
        // Given
        coEvery { authRepository.getProvider() } returns null

        // When
        val result = getProviderUseCase()

        // Then
        assertNull(result)
        coVerify(exactly = 1) { authRepository.getProvider() }
    }
}
