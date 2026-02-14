package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.MyBookSearch
import project.side.domain.model.MyBookSearchItem
import project.side.domain.usecase.mybook.SearchMyBooksUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MyBookSearchViewModelTest {

    @MockK
    private lateinit var searchMyBooksUseCase: SearchMyBooksUseCase

    private lateinit var viewModel: MyBookSearchViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = MyBookSearchViewModel(searchMyBooksUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSearchItem(
        mybookId: Int = 1,
        readingStatus: String = "WISH",
        title: String = "테스트 책"
    ) = MyBookSearchItem(
        mybookId = mybookId,
        readingStatus = readingStatus,
        createdDate = "2025-01-01",
        startedDate = null,
        finishedDate = null,
        title = title,
        author = listOf("저자1"),
        coverImage = null,
        description = "설명"
    )

    @Test
    fun `search returns results successfully`() = runTest {
        // Given
        val items = listOf(createSearchItem(mybookId = 1), createSearchItem(mybookId = 2))
        every { searchMyBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(
                MyBookSearch(totalPages = 1, nowPage = 0, totalElements = 2, books = items)
            )
        )

        // When
        viewModel.search("테스트")

        // Then
        assertEquals(2, viewModel.searchResults.value.size)
        assertEquals("테스트", viewModel.query.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `search with blank query does nothing`() = runTest {
        // When
        viewModel.search("   ")

        // Then
        assertTrue(viewModel.searchResults.value.isEmpty())
        verify(exactly = 0) { searchMyBooksUseCase(any(), any(), any()) }
    }

    @Test
    fun `search error sets loading to false`() = runTest {
        // Given
        every { searchMyBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.error("네트워크 오류")
        )

        // When
        viewModel.search("테스트")

        // Then
        assertTrue(viewModel.searchResults.value.isEmpty())
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `loadMore appends results to existing list`() = runTest {
        // Given - first page with 10 items
        val firstPageItems = (1..10).map { createSearchItem(mybookId = it, title = "책$it") }
        every { searchMyBooksUseCase(any(), eq(0), any()) } returns flowOf(
            DataResource.success(
                MyBookSearch(totalPages = 2, nowPage = 0, totalElements = 15, books = firstPageItems)
            )
        )
        val secondPageItems = (11..15).map { createSearchItem(mybookId = it, title = "책$it") }
        every { searchMyBooksUseCase(any(), eq(1), any()) } returns flowOf(
            DataResource.success(
                MyBookSearch(totalPages = 2, nowPage = 1, totalElements = 15, books = secondPageItems)
            )
        )

        // When
        viewModel.search("책")
        viewModel.loadMore()

        // Then
        assertEquals(15, viewModel.searchResults.value.size)
    }

    @Test
    fun `loadMore does not load when last page reached`() = runTest {
        // Given - less than 10 items (last page)
        val items = listOf(createSearchItem(mybookId = 1))
        every { searchMyBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(
                MyBookSearch(totalPages = 1, nowPage = 0, totalElements = 1, books = items)
            )
        )

        // When
        viewModel.search("테스트")
        viewModel.loadMore()

        // Then - only called once for the initial search
        verify(exactly = 1) { searchMyBooksUseCase(any(), any(), any()) }
    }

    @Test
    fun `new search resets previous results`() = runTest {
        // Given
        val firstResults = listOf(createSearchItem(mybookId = 1, title = "첫번째"))
        every { searchMyBooksUseCase(eq("첫번째"), any(), any()) } returns flowOf(
            DataResource.success(
                MyBookSearch(totalPages = 1, nowPage = 0, totalElements = 1, books = firstResults)
            )
        )
        val secondResults = listOf(createSearchItem(mybookId = 2, title = "두번째"))
        every { searchMyBooksUseCase(eq("두번째"), any(), any()) } returns flowOf(
            DataResource.success(
                MyBookSearch(totalPages = 1, nowPage = 0, totalElements = 1, books = secondResults)
            )
        )

        // When
        viewModel.search("첫번째")
        assertEquals(1, viewModel.searchResults.value.size)
        assertEquals("첫번째", viewModel.searchResults.value[0].title)

        viewModel.search("두번째")

        // Then
        assertEquals(1, viewModel.searchResults.value.size)
        assertEquals("두번째", viewModel.searchResults.value[0].title)
    }
}
