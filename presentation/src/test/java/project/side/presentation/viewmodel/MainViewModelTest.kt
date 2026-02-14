package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook
import project.side.domain.model.HistoryBookInfo
import project.side.domain.model.Member
import project.side.domain.model.StoreBook
import project.side.domain.model.StoreBookItem
import project.side.domain.usecase.GetHistoryBooksUseCase
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import project.side.domain.usecase.mybook.GetStoreBooksUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @MockK
    private lateinit var getLoginStateUseCase: GetLoginStateUseCase

    @MockK
    private lateinit var getMyInfoUseCase: GetMyInfoUseCase

    @MockK
    private lateinit var getStoreBooksUseCase: GetStoreBooksUseCase

    @MockK
    private lateinit var getHistoryBooksUseCase: GetHistoryBooksUseCase

    private lateinit var viewModel: MainViewModel

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

    private fun stubDefaultBooks() {
        every { getStoreBooksUseCase() } returns flowOf(
            DataResource.success(
                StoreBook(
                    content = emptyList(),
                    totalPages = 0, totalElements = 0, last = true, first = true,
                    size = 10, number = 0, numberOfElements = 0, empty = true
                )
            )
        )
        coEvery { getHistoryBooksUseCase() } returns flowOf(
            DataResource.success(
                HistoryBook(totalPages = 0, nowPage = 0, books = emptyList())
            )
        )
    }

    @Test
    fun `when logged in, validates token by calling getMyInfo`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(true)
        every { getMyInfoUseCase() } returns flowOf(
            DataResource.success(Member(nickname = "test"))
        )
        stubDefaultBooks()

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, getHistoryBooksUseCase)

        // Then
        verify(exactly = 1) { getMyInfoUseCase() }
    }

    @Test
    fun `when logged in, nickname is set from getMyInfo response`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(true)
        every { getMyInfoUseCase() } returns flowOf(
            DataResource.success(Member(nickname = "홍길동"))
        )
        stubDefaultBooks()

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, getHistoryBooksUseCase)

        // Then
        assertEquals("홍길동", viewModel.nickname.value)
    }

    @Test
    fun `when not logged in, does not call getMyInfo`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(false)
        stubDefaultBooks()

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, getHistoryBooksUseCase)

        // Then
        verify(exactly = 0) { getMyInfoUseCase() }
    }

    @Test
    fun `fetchBooks loads store books successfully`() = runTest {
        // Given
        val storeBookItems = listOf(
            StoreBookItem(
                mybookId = 1, createdDate = "2025-01-01", title = "테스트 책",
                author = listOf("저자1"), coverImage = null, description = "설명"
            )
        )
        every { getLoginStateUseCase() } returns flowOf(false)
        every { getStoreBooksUseCase() } returns flowOf(
            DataResource.success(
                StoreBook(
                    content = storeBookItems,
                    totalPages = 1, totalElements = 1, last = true, first = true,
                    size = 10, number = 0, numberOfElements = 1, empty = false
                )
            )
        )
        coEvery { getHistoryBooksUseCase() } returns flowOf(
            DataResource.success(
                HistoryBook(totalPages = 0, nowPage = 0, books = emptyList())
            )
        )

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, getHistoryBooksUseCase)

        // Then
        assertEquals(1, viewModel.storeBooks.value.size)
        assertEquals("테스트 책", viewModel.storeBooks.value[0].title)
    }

    @Test
    fun `fetchBooks loads history books successfully`() = runTest {
        // Given
        val historyBookItems = listOf(
            HistoryBookInfo(
                mybookId = 1, title = "히스토리 책",
                coverImage = "https://example.com/cover.jpg",
                startedDate = "2025-01-01", finishedDate = null
            )
        )
        every { getLoginStateUseCase() } returns flowOf(false)
        every { getStoreBooksUseCase() } returns flowOf(
            DataResource.success(
                StoreBook(
                    content = emptyList(),
                    totalPages = 0, totalElements = 0, last = true, first = true,
                    size = 10, number = 0, numberOfElements = 0, empty = true
                )
            )
        )
        coEvery { getHistoryBooksUseCase() } returns flowOf(
            DataResource.success(
                HistoryBook(totalPages = 1, nowPage = 0, books = historyBookItems)
            )
        )

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, getHistoryBooksUseCase)

        // Then
        assertEquals(1, viewModel.historyBooks.value.size)
        assertEquals("히스토리 책", viewModel.historyBooks.value[0].title)
    }
}
