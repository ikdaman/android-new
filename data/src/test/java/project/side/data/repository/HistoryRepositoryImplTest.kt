package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.HistoryDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.HistoryBookEntity
import project.side.data.model.HistoryBookInfoEntity
import project.side.domain.DataResource

class HistoryRepositoryImplTest {

    @MockK
    private lateinit var historyDataSource: HistoryDataSource

    private lateinit var repository: HistoryRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = HistoryRepositoryImpl(historyDataSource)
    }

    @Test
    fun `getHistoryBooks emits loading then success when data source succeeds`() = runTest {
        // Given
        val page = 1
        val limit = 10
        val sort = "desc"
        val historyEntity = HistoryBookEntity(
            totalPages = 5,
            nowPage = 1,
            books = listOf(
                HistoryBookInfoEntity(
                    mybookId = 1,
                    title = "테스트 책",
                    coverImage = "http://example.com/cover.jpg",
                    startedDate = "2024-01-01",
                    finishedDate = "2024-01-31"
                )
            )
        )
        coEvery { historyDataSource.getHistoryBooks(page, limit, sort) } returns DataApiResult.Success(historyEntity)

        // When
        val flow = repository.getHistoryBooks(page, limit, sort)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(5, successData.totalPages)
        assertEquals(1, successData.nowPage)
        assertEquals(1, successData.books.size)
        assertEquals("테스트 책", successData.books[0].title)
        coVerify(exactly = 1) { historyDataSource.getHistoryBooks(page, limit, sort) }
    }

    @Test
    fun `getHistoryBooks emits loading then error when data source returns error`() = runTest {
        // Given
        val page = 1
        val limit = 10
        val sort = "asc"
        val errorMessage = "서버 오류"
        coEvery { historyDataSource.getHistoryBooks(page, limit, sort) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.getHistoryBooks(page, limit, sort)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `getHistoryBooks emits loading then error when exception occurs`() = runTest {
        // Given
        val page = 2
        val limit = 20
        val sort = "recent"
        val exceptionMessage = "네트워크 오류"
        coEvery { historyDataSource.getHistoryBooks(page, limit, sort) } throws RuntimeException(exceptionMessage)

        // When
        val flow = repository.getHistoryBooks(page, limit, sort)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(exceptionMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `getHistoryBooks uses default error message when exception message is null`() = runTest {
        // Given
        val page = 1
        val limit = 10
        val sort = "desc"
        coEvery { historyDataSource.getHistoryBooks(page, limit, sort) } throws RuntimeException()

        // When
        val flow = repository.getHistoryBooks(page, limit, sort)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Error)
        assertEquals("네트워크 오류", (results[1] as DataResource.Error).message)
    }

    @Test
    fun `getHistoryBooks handles empty book list`() = runTest {
        // Given
        val page = 1
        val limit = 10
        val sort = "desc"
        val emptyHistoryEntity = HistoryBookEntity(
            totalPages = 0,
            nowPage = 0,
            books = emptyList()
        )
        coEvery { historyDataSource.getHistoryBooks(page, limit, sort) } returns DataApiResult.Success(emptyHistoryEntity)

        // When
        val flow = repository.getHistoryBooks(page, limit, sort)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(0, successData.totalPages)
        assertEquals(emptyList<Any>(), successData.books)
    }

    @Test
    fun `getHistoryBooks passes correct parameters to data source`() = runTest {
        // Given
        val page = 3
        val limit = 15
        val sort = "oldest"
        val historyEntity = HistoryBookEntity(0, 0, emptyList())
        coEvery { historyDataSource.getHistoryBooks(page, limit, sort) } returns DataApiResult.Success(historyEntity)

        // When
        repository.getHistoryBooks(page, limit, sort).toList()

        // Then
        coVerify(exactly = 1) { historyDataSource.getHistoryBooks(page, limit, sort) }
    }
}
