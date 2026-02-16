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
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.Member
import project.side.domain.model.StoreBook
import project.side.domain.model.StoreBookItem
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import project.side.domain.usecase.mybook.GetStoreBooksUseCase
import project.side.domain.usecase.mybook.UpdateReadingStatusUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @MockK
    private lateinit var getLoginStateUseCase: GetLoginStateUseCase

    @MockK
    private lateinit var getMyInfoUseCase: GetMyInfoUseCase

    @MockK
    private lateinit var getStoreBooksUseCase: GetStoreBooksUseCase

    @MockK
    private lateinit var updateReadingStatusUseCase: UpdateReadingStatusUseCase

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
        every { getStoreBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(
                StoreBook(
                    content = emptyList(),
                    totalPages = 0, totalElements = 0, last = true, first = true,
                    size = 5, number = 0, numberOfElements = 0, empty = true
                )
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
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, updateReadingStatusUseCase)

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
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, updateReadingStatusUseCase)

        // Then
        assertEquals("홍길동", viewModel.nickname.value)
    }

    @Test
    fun `when not logged in, does not call getMyInfo`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(false)
        stubDefaultBooks()

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, updateReadingStatusUseCase)

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
        every { getStoreBooksUseCase(any(), any(), any()) } returns flowOf(
            DataResource.success(
                StoreBook(
                    content = storeBookItems,
                    totalPages = 1, totalElements = 1, last = true, first = true,
                    size = 5, number = 0, numberOfElements = 1, empty = false
                )
            )
        )

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase, getStoreBooksUseCase, updateReadingStatusUseCase)
        viewModel.refreshStoreBooks()

        // Then
        assertEquals(1, viewModel.storeBooks.value.size)
        assertEquals("테스트 책", viewModel.storeBooks.value[0].title)
    }
}
