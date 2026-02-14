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
import project.side.domain.model.StoreBook
import project.side.domain.model.StoreBookItem
import project.side.domain.repository.MyBookRepository

class GetStoreBooksUseCaseTest {
    @MockK
    private lateinit var myBookRepository: MyBookRepository
    private lateinit var useCase: GetStoreBooksUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetStoreBooksUseCase(myBookRepository)
    }

    @Test
    fun `invoke returns success flow from repository`() = runTest {
        // Given
        val keyword = null
        val page = 0
        val size = 10
        val storeBookItem = StoreBookItem(
            mybookId = 1,
            createdDate = "2024-01-01",
            title = "Test Book",
            author = listOf("Test Author"),
            coverImage = "http://example.com/cover.jpg",
            description = "Test description"
        )
        val storeBook = StoreBook(
            content = listOf(storeBookItem),
            totalPages = 1,
            totalElements = 1,
            last = true,
            first = true,
            size = 10,
            number = 0,
            numberOfElements = 1,
            empty = false
        )
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(storeBook))
        coEvery { myBookRepository.getStoreBooks(keyword, page, size) } returns expectedFlow

        // When
        val results = useCase.invoke(keyword, page, size).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(storeBook, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { myBookRepository.getStoreBooks(keyword, page, size) }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val keyword = null
        val page = 0
        val size = 10
        val errorMessage = "Failed to get store books"
        val expectedFlow = flowOf(
            DataResource.Loading<StoreBook>(),
            DataResource.Error(errorMessage)
        )
        coEvery { myBookRepository.getStoreBooks(keyword, page, size) } returns expectedFlow

        // When
        val results = useCase.invoke(keyword, page, size).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { myBookRepository.getStoreBooks(keyword, page, size) }
    }

    @Test
    fun `invoke with keyword filter passes correct parameters`() = runTest {
        // Given
        val keyword = "fiction"
        val page = 1
        val size = 20
        val storeBook = StoreBook(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            last = true,
            first = true,
            size = 20,
            number = 1,
            numberOfElements = 0,
            empty = true
        )
        val expectedFlow = flowOf(DataResource.Success(storeBook))
        coEvery { myBookRepository.getStoreBooks(keyword, page, size) } returns expectedFlow

        // When
        useCase.invoke(keyword, page, size).toList()

        // Then
        coVerify(exactly = 1) { myBookRepository.getStoreBooks(keyword, page, size) }
    }
}
