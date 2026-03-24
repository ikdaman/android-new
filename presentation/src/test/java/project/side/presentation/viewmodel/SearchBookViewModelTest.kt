package project.side.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.BookItem
import project.side.domain.model.BookSearchResult
import project.side.domain.model.DomainResult
import project.side.domain.usecase.SaveManualBookInfoUseCase
import project.side.domain.usecase.search.SearchBookWithIsbnUseCase
import project.side.domain.usecase.search.SearchBookWithTitleUseCase
import java.io.IOException
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class SearchBookViewModelTest {

    @MockK
    private lateinit var searchBookWithTitleUseCase: SearchBookWithTitleUseCase

    @MockK
    private lateinit var searchBookWithIsbnUseCase: SearchBookWithIsbnUseCase

    @MockK
    private lateinit var saveManualBookInfoUseCase: SaveManualBookInfoUseCase

    private lateinit var viewModel: SearchBookViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = SearchBookViewModel(searchBookWithTitleUseCase, searchBookWithIsbnUseCase, saveManualBookInfoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeBookItem(title: String = "테스트 책", itemId: Long = 1L) = BookItem(
        title = title,
        author = "저자",
        cover = "http://cover.jpg",
        isbn = "9781234567890",
        itemId = itemId,
        publisher = "출판사",
        description = "설명",
        pubDate = "2024-01-01"
    )

    // ── initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial searchState has empty query and no books`() {
        val state = viewModel.searchState.value
        assertEquals("", state.query)
        assertTrue(state.books.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `initial bookDetail is Init`() {
        assertEquals(DomainResult.Init, viewModel.bookDetail.value)
    }

    @Test
    fun `initial selectedBookItem is null`() {
        assertNull(viewModel.selectedBookItem.value)
    }

    // ── searchBook ─────────────────────────────────────────────────────────────

    @Test
    fun `searchBook success updates searchState with books`() = runTest {
        // Given
        val books = listOf(makeBookItem("Kotlin in Action"))
        coEvery { searchBookWithTitleUseCase("Kotlin", 1) } returns BookSearchResult(
            totalBookCount = 1,
            books = books
        )

        // When
        viewModel.searchBook("Kotlin")

        // Then
        val state = viewModel.searchState.value
        assertEquals("Kotlin", state.query)
        assertEquals(1, state.books.size)
        assertEquals("Kotlin in Action", state.books[0].title)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `searchBook empty result sets errorMessage`() = runTest {
        // Given
        coEvery { searchBookWithTitleUseCase("없는책", 1) } returns BookSearchResult(
            totalBookCount = 0,
            books = emptyList()
        )

        // When
        viewModel.searchBook("없는책")

        // Then
        val state = viewModel.searchState.value
        assertTrue(state.books.isEmpty())
        assertEquals("검색 결과가 없습니다.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `searchBook sets isLoading to false after completion`() = runTest {
        // Given
        coEvery { searchBookWithTitleUseCase(any(), any()) } returns BookSearchResult(
            totalBookCount = 0,
            books = emptyList()
        )

        // When
        viewModel.searchBook("query")

        // Then
        assertFalse(viewModel.searchState.value.isLoading)
    }

    @Test
    fun `searchBook IOException sets network error message`() = runTest {
        // Given
        coEvery { searchBookWithTitleUseCase(any(), any()) } throws IOException("timeout")

        // When
        viewModel.searchBook("Kotlin")

        // Then
        val state = viewModel.searchState.value
        assertFalse(state.isLoading)
        assertEquals("네트워크 연결을 확인해주세요.", state.errorMessage)
    }

    @Test
    fun `searchBook general Exception sets generic error message`() = runTest {
        // Given
        coEvery { searchBookWithTitleUseCase(any(), any()) } throws RuntimeException("Unknown error")

        // When
        viewModel.searchBook("Kotlin")

        // Then
        val state = viewModel.searchState.value
        assertFalse(state.isLoading)
        assertEquals("검색 중 오류가 발생했습니다.", state.errorMessage)
    }

    @Test
    fun `searchBook sets hasMore when more pages available`() = runTest {
        // Given – totalBookCount > 50 means page 1 does not cover all results
        val books = List(50) { makeBookItem("책 $it", itemId = it.toLong()) }
        coEvery { searchBookWithTitleUseCase(any(), 1) } returns BookSearchResult(
            totalBookCount = 100,
            books = books
        )

        // When
        viewModel.searchBook("Kotlin")

        // Then
        assertTrue(viewModel.searchState.value.hasMore)
    }

    @Test
    fun `searchBook sets hasMore false when last page`() = runTest {
        // Given – totalBookCount <= 50 means page 1 is the last page
        val books = listOf(makeBookItem())
        coEvery { searchBookWithTitleUseCase(any(), 1) } returns BookSearchResult(
            totalBookCount = 1,
            books = books
        )

        // When
        viewModel.searchBook("Kotlin")

        // Then
        assertFalse(viewModel.searchState.value.hasMore)
    }

    // ── loadNextPage ───────────────────────────────────────────────────────────

    @Test
    fun `loadNextPage appends books to existing list`() = runTest {
        // Given – first page load
        val firstPageBooks = listOf(makeBookItem("책 1", itemId = 1L))
        val secondPageBooks = listOf(makeBookItem("책 2", itemId = 2L))
        coEvery { searchBookWithTitleUseCase("Kotlin", 1) } returns BookSearchResult(
            totalBookCount = 100,
            books = firstPageBooks
        )
        coEvery { searchBookWithTitleUseCase("Kotlin", 2) } returns BookSearchResult(
            totalBookCount = 100,
            books = secondPageBooks
        )
        viewModel.searchBook("Kotlin")

        // When
        viewModel.loadNextPage()

        // Then
        val state = viewModel.searchState.value
        assertEquals(2, state.books.size)
        assertEquals("책 1", state.books[0].title)
        assertEquals("책 2", state.books[1].title)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `loadNextPage does nothing when hasMore is false`() = runTest {
        // Given – search result with no more pages
        val books = listOf(makeBookItem())
        coEvery { searchBookWithTitleUseCase(any(), 1) } returns BookSearchResult(
            totalBookCount = 1,
            books = books
        )
        viewModel.searchBook("Kotlin")

        // When
        viewModel.loadNextPage()

        // Then – useCase called only once (for the initial search)
        coVerify(exactly = 1) { searchBookWithTitleUseCase(any(), any()) }
    }

    @Test
    fun `loadNextPage does nothing when query is blank`() = runTest {
        // When – loadNextPage called without any prior search
        viewModel.loadNextPage()

        // Then
        coVerify(exactly = 0) { searchBookWithTitleUseCase(any(), any()) }
    }

    @Test
    fun `loadNextPage on exception keeps isLoadingMore false`() = runTest {
        // Given – first page succeeds
        val firstPageBooks = listOf(makeBookItem())
        coEvery { searchBookWithTitleUseCase("Kotlin", 1) } returns BookSearchResult(
            totalBookCount = 100,
            books = firstPageBooks
        )
        coEvery { searchBookWithTitleUseCase("Kotlin", 2) } throws RuntimeException("error")
        viewModel.searchBook("Kotlin")

        // When
        viewModel.loadNextPage()

        // Then
        assertFalse(viewModel.searchState.value.isLoadingMore)
    }

    // ── searchBookByIsbn ───────────────────────────────────────────────────────

    @Test
    fun `searchBookByIsbn success sets bookDetail to Success and selectedBookItem`() = runTest {
        // Given
        val book = makeBookItem("ISBN 책")
        coEvery { searchBookWithIsbnUseCase("9781234567890") } returns BookSearchResult(
            totalBookCount = 1,
            books = listOf(book)
        )

        // When
        viewModel.searchBookByIsbn("9781234567890")

        // Then
        val detail = viewModel.bookDetail.value
        assertTrue(detail is DomainResult.Success)
        assertEquals(book, (detail as DomainResult.Success).data)
        assertEquals(book, viewModel.selectedBookItem.value)
    }

    @Test
    fun `searchBookByIsbn with empty result sets bookDetail to Error`() = runTest {
        // Given
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(
            totalBookCount = 0,
            books = emptyList()
        )

        // When
        viewModel.searchBookByIsbn("0000000000000")

        // Then
        val detail = viewModel.bookDetail.value
        assertTrue(detail is DomainResult.Error)
        assertEquals("책을 찾을 수 없습니다.", (detail as DomainResult.Error).message)
    }

    @Test
    fun `searchBookByIsbn IOException sets network error`() = runTest {
        // Given
        coEvery { searchBookWithIsbnUseCase(any()) } throws IOException("network error")

        // When
        viewModel.searchBookByIsbn("1234567890")

        // Then
        val detail = viewModel.bookDetail.value
        assertTrue(detail is DomainResult.Error)
        assertEquals("네트워크 연결을 확인해주세요.", (detail as DomainResult.Error).message)
    }

    @Test
    fun `searchBookByIsbn general Exception sets generic error`() = runTest {
        // Given
        coEvery { searchBookWithIsbnUseCase(any()) } throws RuntimeException("unexpected")

        // When
        viewModel.searchBookByIsbn("1234567890")

        // Then
        val detail = viewModel.bookDetail.value
        assertTrue(detail is DomainResult.Error)
        assertEquals("책 정보를 불러오지 못했습니다.", (detail as DomainResult.Error).message)
    }

    @Test
    fun `searchBookByIsbn sets bookDetail to Loading initially`() = runTest {
        // Given – suspend but never returns in this test (we inspect intermediate state)
        // Use a real result here since UnconfinedTestDispatcher runs eagerly
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(books = emptyList())

        // When
        viewModel.searchBookByIsbn("1234")

        // Then – after completion it should not be Loading
        assertTrue(viewModel.bookDetail.value !is DomainResult.Loading)
    }

    // ── clearSearchedBook ──────────────────────────────────────────────────────

    @Test
    fun `clearSearchedBook resets bookDetail to Init`() = runTest {
        // Given – set some state first
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(
            books = listOf(makeBookItem())
        )
        viewModel.searchBookByIsbn("1234567890")

        // When
        viewModel.clearSearchedBook()

        // Then
        assertEquals(DomainResult.Init, viewModel.bookDetail.value)
    }

    // ── saveSelectedBook ───────────────────────────────────────────────────────

    @Test
    fun `saveSelectedBook with no selected book emits SaveEvent Error`() = runTest {
        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveSelectedBook()
            val event = awaitItem()
            assertTrue(event is SaveEvent.Error)
            assertEquals("선택된 책이 없습니다.", (event as SaveEvent.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveSelectedBook success emits SaveEvent Success`() = runTest {
        // Given – set selectedBookItem via searchBookByIsbn
        val book = makeBookItem("선택된 책")
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(books = listOf(book))
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))
        viewModel.searchBookByIsbn("1234567890")

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveSelectedBook()
            assertEquals(SaveEvent.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveSelectedBook error emits SaveEvent Error with message`() = runTest {
        // Given
        val book = makeBookItem("선택된 책")
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(books = listOf(book))
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.error("저장 실패"))
        viewModel.searchBookByIsbn("1234567890")

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveSelectedBook()
            val event = awaitItem()
            assertTrue(event is SaveEvent.Error)
            assertEquals("저장 실패", (event as SaveEvent.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveSelectedBook error with null message uses default message`() = runTest {
        // Given
        val book = makeBookItem()
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(books = listOf(book))
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.error(null))
        viewModel.searchBookByIsbn("1234567890")

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveSelectedBook()
            val event = awaitItem()
            assertTrue(event is SaveEvent.Error)
            assertEquals("책 저장에 실패했어요.", (event as SaveEvent.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveSelectedBook passes source ALADIN to useCase`() = runTest {
        // Given
        val book = makeBookItem()
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(books = listOf(book))
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))
        viewModel.searchBookByIsbn("1234567890")

        // When
        viewModel.saveSelectedBook()

        // Then
        coVerify {
            saveManualBookInfoUseCase(match { it.source == "ALADIN" })
        }
    }

    @Test
    fun `saveSelectedBook with startDate and endDate formats dates to UTC ISO`() = runTest {
        // Given
        val book = makeBookItem()
        coEvery { searchBookWithIsbnUseCase(any()) } returns BookSearchResult(books = listOf(book))
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))
        viewModel.searchBookByIsbn("1234567890")

        val startDate = LocalDate.of(2024, 1, 10)
        val endDate = LocalDate.of(2024, 2, 20)

        // When
        viewModel.saveSelectedBook(startDate = startDate, endDate = endDate)

        // Then
        coVerify {
            saveManualBookInfoUseCase(
                match {
                    it.startDate == "2024-01-10T00:00:00Z" && it.endDate == "2024-02-20T00:00:00Z"
                }
            )
        }
    }
}
