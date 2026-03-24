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
import project.side.domain.model.DomainAuthEvent
import project.side.domain.repository.AuthEventRepository

class GetAuthEventUseCaseTest {
    @MockK
    private lateinit var authEventRepository: AuthEventRepository
    private lateinit var useCase: GetAuthEventUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetAuthEventUseCase(authEventRepository)
    }

    @Test
    fun `invoke returns auth event flow from repository`() = runTest {
        // Given
        val expectedFlow = flowOf(DomainAuthEvent.LOGIN_REQUIRED)
        every { authEventRepository.getAuthEvents() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(1, results.size)
        assertEquals(DomainAuthEvent.LOGIN_REQUIRED, results[0])
        verify(exactly = 1) { authEventRepository.getAuthEvents() }
    }

    @Test
    fun `invoke returns multiple auth events from repository`() = runTest {
        // Given
        val expectedFlow = flowOf(
            DomainAuthEvent.LOGIN_REQUIRED,
            DomainAuthEvent.LOGIN_REQUIRED
        )
        every { authEventRepository.getAuthEvents() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(2, results.size)
        results.forEach { assertEquals(DomainAuthEvent.LOGIN_REQUIRED, it) }
        verify(exactly = 1) { authEventRepository.getAuthEvents() }
    }

    @Test
    fun `invoke delegates to repository getAuthEvents`() = runTest {
        // Given
        every { authEventRepository.getAuthEvents() } returns flowOf()

        // When
        useCase.invoke().toList()

        // Then
        verify(exactly = 1) { authEventRepository.getAuthEvents() }
    }
}
