package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook
import project.side.domain.model.HistoryBookInfo
import project.side.domain.usecase.GetHistoryBooksUseCase
import project.side.presentation.model.HistoryViewType

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @MockK
    private lateinit var getHistoryBooksUseCase: GetHistoryBooksUseCase

    private lateinit var viewModel: HistoryViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createHistoryBookInfo(mybookId: Int = 1, title: String = "테스트 책") =
        HistoryBookInfo(
            mybookId = mybookId,
            title = title,
            author = listOf("저자1"),
            coverImage = "https://example.com/cover.jpg",
            description = "설명",
            startedDate = "2025-01-01",
            finishedDate = "2025-01-15"
        )

    @Test
    fun `init loads history books successfully`() = runTest {
        // Given
        val books = listOf(createHistoryBookInfo(mybookId = 1), createHistoryBookInfo(mybookId = 2))
        coEvery { getHistoryBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(HistoryBook(totalPages = 1, nowPage = 0, books = books))
        )

        // When
        viewModel = HistoryViewModel(getHistoryBooksUseCase)

        // Then
        assertEquals(2, viewModel.uiState.value.books.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `error sets error message`() = runTest {
        // Given
        coEvery { getHistoryBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.error("네트워크 오류")
        )

        // When
        viewModel = HistoryViewModel(getHistoryBooksUseCase)

        // Then
        assertEquals("네트워크 오류", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadMore appends books`() = runTest {
        // Given - first page
        val firstBooks = (1..5).map { createHistoryBookInfo(mybookId = it) }
        coEvery { getHistoryBooksUseCase(any(), eq(0), any()) } returns flowOf(
            DataResource.success(HistoryBook(totalPages = 2, nowPage = 0, books = firstBooks))
        )
        val secondBooks = (6..10).map { createHistoryBookInfo(mybookId = it) }
        coEvery { getHistoryBooksUseCase(any(), eq(1), any()) } returns flowOf(
            DataResource.success(HistoryBook(totalPages = 2, nowPage = 1, books = secondBooks))
        )

        // When
        viewModel = HistoryViewModel(getHistoryBooksUseCase)
        viewModel.loadMore()

        // Then
        assertEquals(10, viewModel.uiState.value.books.size)
    }

    @Test
    fun `loadMore does not load when last page`() = runTest {
        // Given - single page (nowPage 0, totalPages 1)
        val books = listOf(createHistoryBookInfo())
        coEvery { getHistoryBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(HistoryBook(totalPages = 1, nowPage = 0, books = books))
        )

        // When
        viewModel = HistoryViewModel(getHistoryBooksUseCase)
        val sizeBeforeLoadMore = viewModel.uiState.value.books.size
        viewModel.loadMore()

        // Then - size should not change
        assertEquals(sizeBeforeLoadMore, viewModel.uiState.value.books.size)
    }

    @Test
    fun `onViewTypeChanged toggles view type`() = runTest {
        // Given
        coEvery { getHistoryBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(HistoryBook(totalPages = 1, nowPage = 0, books = emptyList()))
        )
        viewModel = HistoryViewModel(getHistoryBooksUseCase)

        // When
        assertEquals(HistoryViewType.LIST, viewModel.uiState.value.viewType)
        viewModel.onViewTypeChanged()

        // Then
        assertEquals(HistoryViewType.DATASET, viewModel.uiState.value.viewType)
    }
}
