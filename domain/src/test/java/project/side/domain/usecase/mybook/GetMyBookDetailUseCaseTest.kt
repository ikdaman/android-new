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
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo
import project.side.domain.repository.MyBookRepository

class GetMyBookDetailUseCaseTest {
    @MockK
    private lateinit var myBookRepository: MyBookRepository
    private lateinit var useCase: GetMyBookDetailUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetMyBookDetailUseCase(myBookRepository)
    }

    @Test
    fun `invoke returns success flow with detail data`() = runTest {
        // Given
        val mybookId = 123
        val bookInfo = MyBookDetailBookInfo(
            bookId = "book-001",
            source = "aladin",
            title = "Test Book",
            author = "Test Author",
            coverImage = "http://example.com/cover.jpg",
            publisher = "Test Publisher",
            totalPage = 300,
            publishDate = "2024-01-01",
            isbn = "1234567890",
            aladinId = "A12345"
        )
        val historyInfo = MyBookDetailHistoryInfo(
            startedDate = "2024-01-15",
            finishedDate = "2024-02-01"
        )
        val myBookDetail = MyBookDetail(
            mybookId = "123",
            readingStatus = "COMPLETED",
            shelfType = "COLLECTION",
            createdDate = "2024-01-10",
            reason = "Great book!",
            bookInfo = bookInfo,
            historyInfo = historyInfo
        )
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(myBookDetail))
        coEvery { myBookRepository.getMyBookDetail(mybookId) } returns expectedFlow

        // When
        val results = useCase.invoke(mybookId).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(myBookDetail, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { myBookRepository.getMyBookDetail(mybookId) }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val mybookId = 123
        val errorMessage = "Failed to get book detail"
        val expectedFlow = flowOf(
            DataResource.Loading<MyBookDetail>(),
            DataResource.Error(errorMessage)
        )
        coEvery { myBookRepository.getMyBookDetail(mybookId) } returns expectedFlow

        // When
        val results = useCase.invoke(mybookId).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { myBookRepository.getMyBookDetail(mybookId) }
    }

    @Test
    fun `invoke passes correct mybookId to repository`() = runTest {
        // Given
        val mybookId = 456
        val bookInfo = MyBookDetailBookInfo(
            bookId = "book-002",
            source = "aladin",
            title = "Another Book",
            author = "Another Author",
            coverImage = null,
            publisher = null,
            totalPage = null,
            publishDate = null,
            isbn = null,
            aladinId = null
        )
        val historyInfo = MyBookDetailHistoryInfo(startedDate = null, finishedDate = null)
        val myBookDetail = MyBookDetail(
            mybookId = "456",
            readingStatus = "READING",
            shelfType = "WISHLIST",
            createdDate = "2024-02-01",
            reason = null,
            bookInfo = bookInfo,
            historyInfo = historyInfo
        )
        val expectedFlow = flowOf(DataResource.Success(myBookDetail))
        coEvery { myBookRepository.getMyBookDetail(mybookId) } returns expectedFlow

        // When
        useCase.invoke(mybookId).toList()

        // Then
        coVerify(exactly = 1) { myBookRepository.getMyBookDetail(mybookId) }
    }
}
