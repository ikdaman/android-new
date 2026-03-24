package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.data.model.AuthEvent
import project.side.data.model.DataAuthEvent
import project.side.domain.model.DomainAuthEvent

class AuthEventRepositoryImplTest {

    private lateinit var repository: AuthEventRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AuthEventRepositoryImpl()
    }

    @Test
    fun `getAuthEvents maps DataAuthEvent LOGIN_REQUIRED to DomainAuthEvent LOGIN_REQUIRED`() = runTest {
        // Given
        val results = mutableListOf<DomainAuthEvent>()

        // When
        val job = launch {
            repository.getAuthEvents().toList(results)
        }

        AuthEvent.notify(DataAuthEvent.LOGIN_REQUIRED)

        // Allow the flow to emit
        testScheduler.advanceUntilIdle()
        job.cancel()

        // Then
        assertEquals(1, results.size)
        assertEquals(DomainAuthEvent.LOGIN_REQUIRED, results[0])
    }

    @Test
    fun `getAuthEvents emits correct domain event when notified`() = runTest {
        // When
        val job = launch {
            val event = repository.getAuthEvents().first()

            // Then
            assertEquals(DomainAuthEvent.LOGIN_REQUIRED, event)
        }

        AuthEvent.notify(DataAuthEvent.LOGIN_REQUIRED)
        testScheduler.advanceUntilIdle()
        job.cancel()
    }

    @Test
    fun `getAuthEvents maps multiple LOGIN_REQUIRED events correctly`() = runTest {
        // Given
        val results = mutableListOf<DomainAuthEvent>()

        // When
        val job = launch {
            repository.getAuthEvents().toList(results)
        }

        AuthEvent.notify(DataAuthEvent.LOGIN_REQUIRED)
        AuthEvent.notify(DataAuthEvent.LOGIN_REQUIRED)
        testScheduler.advanceUntilIdle()
        job.cancel()

        // Then: at least one event was received and all are LOGIN_REQUIRED
        assertTrue(results.isNotEmpty())
        results.forEach { event ->
            assertEquals(DomainAuthEvent.LOGIN_REQUIRED, event)
        }
    }
}

private fun assertTrue(value: Boolean) {
    org.junit.Assert.assertTrue(value)
}
