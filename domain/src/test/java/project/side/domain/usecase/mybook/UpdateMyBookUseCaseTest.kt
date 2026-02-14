package project.side.domain.usecase.mybook

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
import project.side.domain.DataResource
import project.side.domain.repository.MyBookRepository

class UpdateMyBookUseCaseTest {
    @MockK
    private lateinit var myBookRepository: MyBookRepository
    private lateinit var useCase: UpdateMyBookUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateMyBookUseCase(myBookRepository)
    }

    @Test
    fun `invoke returns success flow with mybookId`() = runTest {
        // Given
        val mybookId = 123
        val reason = "Great book!"
        val startedDate = "2024-01-01"
        val finishedDate = "2024-02-01"
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
        } returns expectedFlow

        // When
        val results = useCase.invoke(mybookId, reason, startedDate, finishedDate).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(mybookId, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
        }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val mybookId = 123
        val reason = null
        val startedDate = "2024-01-01"
        val finishedDate = null
        val errorMessage = "Failed to update book"
        val expectedFlow = flowOf(
            DataResource.Loading<Int>(),
            DataResource.Error(errorMessage)
        )
        coEvery {
            myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
        } returns expectedFlow

        // When
        val results = useCase.invoke(mybookId, reason, startedDate, finishedDate).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
        }
    }

    @Test
    fun `invoke passes all parameters correctly to repository`() = runTest {
        // Given
        val mybookId = 456
        val reason = "Interesting read"
        val startedDate = "2024-03-01"
        val finishedDate = "2024-03-20"
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
        } returns expectedFlow

        // When
        useCase.invoke(mybookId, reason, startedDate, finishedDate).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
        }
    }

    @Test
    fun `invoke with null optional parameters passes nulls to repository`() = runTest {
        // Given
        val mybookId = 789
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(mybookId, null, null, null)
        } returns expectedFlow

        // When
        useCase.invoke(mybookId).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(mybookId, null, null, null)
        }
    }
}
