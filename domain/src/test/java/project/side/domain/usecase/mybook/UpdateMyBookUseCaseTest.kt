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
        val status = "HISTORY"
        val reason = "Great book!"
        val startedDate = "2024-01-01"
        val finishedDate = "2024-02-01"
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        } returns expectedFlow

        // When
        val results = useCase.invoke(
            mybookId = mybookId,
            status = status,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate
        ).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(mybookId, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val mybookId = 123
        val status = null
        val reason = null
        val startedDate = "2024-01-01"
        val finishedDate = null
        val errorMessage = "Failed to update book"
        val expectedFlow = flowOf(
            DataResource.Loading<Int>(),
            DataResource.Error(errorMessage)
        )
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        } returns expectedFlow

        // When
        val results = useCase.invoke(
            mybookId = mybookId,
            status = status,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate
        ).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        }
    }

    @Test
    fun `invoke passes all parameters correctly to repository`() = runTest {
        // Given
        val mybookId = 456
        val status = "READING"
        val reason = "Interesting read"
        val startedDate = "2024-03-01"
        val finishedDate = "2024-03-20"
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        } returns expectedFlow

        // When
        useCase.invoke(
            mybookId = mybookId,
            status = status,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate
        ).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        }
    }

    @Test
    fun `invoke with null optional parameters passes nulls to repository`() = runTest {
        // Given
        val mybookId = 789
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = null,
                reason = null,
                startedDate = null,
                finishedDate = null,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        } returns expectedFlow

        // When
        useCase.invoke(mybookId = mybookId).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = null,
                reason = null,
                startedDate = null,
                finishedDate = null,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        }
    }

    @Test
    fun `invoke with status passes status to repository`() = runTest {
        // Given
        val mybookId = 100
        val status = "HISTORY"
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = null,
                startedDate = null,
                finishedDate = null,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        } returns expectedFlow

        // When
        useCase.invoke(mybookId = mybookId, status = status).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = null,
                startedDate = null,
                finishedDate = null,
                bookInfoTitle = null,
                bookInfoAuthor = null,
                bookInfoPublisher = null,
                bookInfoPublishDate = null,
                bookInfoIsbn = null,
                bookInfoTotalPage = null
            )
        }
    }

    @Test
    fun `invoke with bookInfo params passes them to repository`() = runTest {
        // Given
        val mybookId = 200
        val bookInfoTitle = "The Great Book"
        val bookInfoAuthor = "John Doe"
        val bookInfoPublisher = "Publisher Inc"
        val bookInfoPublishDate = "2024-01-15"
        val bookInfoIsbn = "978-1234567890"
        val bookInfoTotalPage = 350
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = null,
                reason = null,
                startedDate = null,
                finishedDate = null,
                bookInfoTitle = bookInfoTitle,
                bookInfoAuthor = bookInfoAuthor,
                bookInfoPublisher = bookInfoPublisher,
                bookInfoPublishDate = bookInfoPublishDate,
                bookInfoIsbn = bookInfoIsbn,
                bookInfoTotalPage = bookInfoTotalPage
            )
        } returns expectedFlow

        // When
        useCase.invoke(
            mybookId = mybookId,
            bookInfoTitle = bookInfoTitle,
            bookInfoAuthor = bookInfoAuthor,
            bookInfoPublisher = bookInfoPublisher,
            bookInfoPublishDate = bookInfoPublishDate,
            bookInfoIsbn = bookInfoIsbn,
            bookInfoTotalPage = bookInfoTotalPage
        ).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = null,
                reason = null,
                startedDate = null,
                finishedDate = null,
                bookInfoTitle = bookInfoTitle,
                bookInfoAuthor = bookInfoAuthor,
                bookInfoPublisher = bookInfoPublisher,
                bookInfoPublishDate = bookInfoPublishDate,
                bookInfoIsbn = bookInfoIsbn,
                bookInfoTotalPage = bookInfoTotalPage
            )
        }
    }

    @Test
    fun `invoke with all parameters passes everything correctly`() = runTest {
        // Given
        val mybookId = 300
        val status = "READING"
        val reason = "Must-read classic"
        val startedDate = "2024-02-01"
        val finishedDate = "2024-02-20"
        val bookInfoTitle = "Complete Guide"
        val bookInfoAuthor = "Jane Smith"
        val bookInfoPublisher = "Tech Press"
        val bookInfoPublishDate = "2023-12-10"
        val bookInfoIsbn = "978-9876543210"
        val bookInfoTotalPage = 500
        val expectedFlow = flowOf(DataResource.Success(mybookId))
        coEvery {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = bookInfoTitle,
                bookInfoAuthor = bookInfoAuthor,
                bookInfoPublisher = bookInfoPublisher,
                bookInfoPublishDate = bookInfoPublishDate,
                bookInfoIsbn = bookInfoIsbn,
                bookInfoTotalPage = bookInfoTotalPage
            )
        } returns expectedFlow

        // When
        useCase.invoke(
            mybookId = mybookId,
            status = status,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate,
            bookInfoTitle = bookInfoTitle,
            bookInfoAuthor = bookInfoAuthor,
            bookInfoPublisher = bookInfoPublisher,
            bookInfoPublishDate = bookInfoPublishDate,
            bookInfoIsbn = bookInfoIsbn,
            bookInfoTotalPage = bookInfoTotalPage
        ).toList()

        // Then
        coVerify(exactly = 1) {
            myBookRepository.updateMyBook(
                mybookId = mybookId,
                status = status,
                reason = reason,
                startedDate = startedDate,
                finishedDate = finishedDate,
                bookInfoTitle = bookInfoTitle,
                bookInfoAuthor = bookInfoAuthor,
                bookInfoPublisher = bookInfoPublisher,
                bookInfoPublishDate = bookInfoPublishDate,
                bookInfoIsbn = bookInfoIsbn,
                bookInfoTotalPage = bookInfoTotalPage
            )
        }
    }
}
