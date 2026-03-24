package project.side.domain.usecase

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
import project.side.domain.repository.UserRepository

class GetLoginStateUseCaseTest {
    @MockK
    private lateinit var userRepository: UserRepository
    private lateinit var useCase: GetLoginStateUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetLoginStateUseCase(userRepository)
    }

    @Test
    fun `invoke returns true when user is logged in`() = runTest {
        // Given
        every { userRepository.isLoggedIn() } returns flowOf(true)

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(1, results.size)
        assertEquals(true, results[0])
        verify(exactly = 1) { userRepository.isLoggedIn() }
    }

    @Test
    fun `invoke returns false when user is not logged in`() = runTest {
        // Given
        every { userRepository.isLoggedIn() } returns flowOf(false)

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(1, results.size)
        assertEquals(false, results[0])
        verify(exactly = 1) { userRepository.isLoggedIn() }
    }

    @Test
    fun `invoke returns multiple login state changes from repository`() = runTest {
        // Given
        val expectedFlow = flowOf(false, true)
        every { userRepository.isLoggedIn() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(2, results.size)
        assertEquals(false, results[0])
        assertEquals(true, results[1])
        verify(exactly = 1) { userRepository.isLoggedIn() }
    }

    @Test
    fun `invoke delegates to repository isLoggedIn`() = runTest {
        // Given
        every { userRepository.isLoggedIn() } returns flowOf(true)

        // When
        useCase.invoke().toList()

        // Then
        verify(exactly = 1) { userRepository.isLoggedIn() }
    }
}
