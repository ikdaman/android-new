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
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook
import project.side.domain.model.HistoryBookInfo
import project.side.domain.repository.HistoryRepository

class GetHistoryBooksUseCaseTest {

    @MockK
    private lateinit var historyRepository: HistoryRepository

    private lateinit var useCase: GetHistoryBooksUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetHistoryBooksUseCase(historyRepository)
    }

    @Test
    fun `invoke returns flow from repository with correct params`() = runTest {
        // Given
        val keyword = "test"
        val page = 1
        val size = 10
        val historyBook = HistoryBook(
            totalPages = 5,
            nowPage = 1,
            books = listOf(
                HistoryBookInfo(
                    mybookId = 1,
                    title = "테스트 책",
                    author = listOf("저자"),
                    coverImage = "http://example.com/cover.jpg",
                    description = "설명",
                    startedDate = "2024-01-01",
                    finishedDate = "2024-01-31"
                )
            )
        )
        val expectedFlow = flowOf(
            DataResource.Loading(),
            DataResource.Success(historyBook)
        )
        coEvery { historyRepository.getHistoryBooks(keyword, page, size) } returns expectedFlow

        // When
        val resultFlow = useCase.invoke(keyword, page, size)
        val results = resultFlow.toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(historyBook, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { historyRepository.getHistoryBooks(keyword, page, size) }
    }

    @Test
    fun `invoke returns error flow when repository fails`() = runTest {
        // Given
        val keyword = null
        val page = 1
        val size = 20
        val errorMessage = "네트워크 오류"
        val expectedFlow = flowOf(
            DataResource.Loading(),
            DataResource.Error(errorMessage)
        )
        coEvery { historyRepository.getHistoryBooks(keyword, page, size) } returns expectedFlow

        // When
        val resultFlow = useCase.invoke(keyword, page, size)
        val results = resultFlow.toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `invoke passes all parameters correctly to repository`() = runTest {
        // Given
        val keyword = "history"
        val page = 3
        val size = 15
        val emptyHistory = HistoryBook(totalPages = 0, nowPage = 0, books = emptyList())
        coEvery { historyRepository.getHistoryBooks(keyword, page, size) } returns flowOf(
            DataResource.Success(emptyHistory)
        )

        // When
        useCase.invoke(keyword, page, size)

        // Then
        coVerify(exactly = 1) { historyRepository.getHistoryBooks(keyword, page, size) }
    }
}
