package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.AuthDataStoreSource

class UserRepositoryImplTest {

    @MockK
    private lateinit var authDataStoreSource: AuthDataStoreSource

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = UserRepositoryImpl(authDataStoreSource)
    }

    @Test
    fun `isLoggedIn returns true when user is logged in`() = runTest {
        // Given
        every { authDataStoreSource.isLoggedIn() } returns flowOf(true)

        // When
        val results = repository.isLoggedIn().toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0])
        verify(exactly = 1) { authDataStoreSource.isLoggedIn() }
    }

    @Test
    fun `isLoggedIn returns false when user is not logged in`() = runTest {
        // Given
        every { authDataStoreSource.isLoggedIn() } returns flowOf(false)

        // When
        val results = repository.isLoggedIn().toList()

        // Then
        assertEquals(1, results.size)
        assertEquals(false, results[0])
        verify(exactly = 1) { authDataStoreSource.isLoggedIn() }
    }

    @Test
    fun `isLoggedIn delegates directly to authDataStoreSource`() = runTest {
        // Given
        every { authDataStoreSource.isLoggedIn() } returns flowOf(true)

        // When
        repository.isLoggedIn().toList()

        // Then
        verify(exactly = 1) { authDataStoreSource.isLoggedIn() }
    }

    @Test
    fun `isLoggedIn emits multiple values from authDataStoreSource`() = runTest {
        // Given
        every { authDataStoreSource.isLoggedIn() } returns flowOf(false, true, false)

        // When
        val results = repository.isLoggedIn().toList()

        // Then
        assertEquals(3, results.size)
        assertEquals(false, results[0])
        assertEquals(true, results[1])
        assertEquals(false, results[2])
    }
}
