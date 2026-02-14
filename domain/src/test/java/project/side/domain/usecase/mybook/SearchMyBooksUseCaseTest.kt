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
import project.side.domain.model.MyBookSearch
import project.side.domain.model.MyBookSearchItem
import project.side.domain.repository.MyBookRepository

class SearchMyBooksUseCaseTest {
    @MockK
    private lateinit var myBookRepository: MyBookRepository
    private lateinit var useCase: SearchMyBooksUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SearchMyBooksUseCase(myBookRepository)
    }

    @Test
    fun `invoke returns success flow with search results`() = runTest {
        // Given
        val query = "test"
        val page = 0
        val size = 10
        val searchItem = MyBookSearchItem(
            mybookId = 1,
            readingStatus = "READING",
            createdDate = "2024-01-01",
            startedDate = "2024-01-05",
            finishedDate = null,
            title = "Test Book",
            author = listOf("Test Author"),
            coverImage = "http://example.com/cover.jpg",
            description = "Test description"
        )
        val searchResult = MyBookSearch(
            totalPages = 1,
            nowPage = 0,
            totalElements = 1,
            books = listOf(searchItem)
        )
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(searchResult))
        coEvery { myBookRepository.searchMyBooks(query, page, size) } returns expectedFlow

        // When
        val results = useCase.invoke(query, page, size).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(searchResult, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { myBookRepository.searchMyBooks(query, page, size) }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val query = "test"
        val page = 0
        val size = 10
        val errorMessage = "Search failed"
        val expectedFlow = flowOf(
            DataResource.Loading<MyBookSearch>(),
            DataResource.Error(errorMessage)
        )
        coEvery { myBookRepository.searchMyBooks(query, page, size) } returns expectedFlow

        // When
        val results = useCase.invoke(query, page, size).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { myBookRepository.searchMyBooks(query, page, size) }
    }

    @Test
    fun `invoke with default params passes null page and size`() = runTest {
        // Given
        val query = "test"
        val searchResult = MyBookSearch(
            totalPages = 1,
            nowPage = 0,
            totalElements = 0,
            books = emptyList()
        )
        val expectedFlow = flowOf(DataResource.Success(searchResult))
        coEvery { myBookRepository.searchMyBooks(query, null, null) } returns expectedFlow

        // When
        useCase.invoke(query).toList()

        // Then
        coVerify(exactly = 1) { myBookRepository.searchMyBooks(query, null, null) }
    }
}
