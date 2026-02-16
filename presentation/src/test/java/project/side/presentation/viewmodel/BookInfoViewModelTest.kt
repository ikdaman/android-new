package project.side.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo
import project.side.domain.usecase.mybook.DeleteMyBookUseCase
import project.side.domain.usecase.mybook.GetMyBookDetailUseCase
import project.side.domain.usecase.mybook.UpdateMyBookUseCase
import project.side.presentation.util.SnackbarManager

@OptIn(ExperimentalCoroutinesApi::class)
class BookInfoViewModelTest {

    @MockK
    private lateinit var getMyBookDetailUseCase: GetMyBookDetailUseCase

    @MockK
    private lateinit var deleteMyBookUseCase: DeleteMyBookUseCase

    @MockK
    private lateinit var updateMyBookUseCase: UpdateMyBookUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        mockkObject(SnackbarManager)
        coEvery { SnackbarManager.show(any()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createDetail(mybookId: String = "123"): MyBookDetail = MyBookDetail(
        mybookId = mybookId,
        readingStatus = "COMPLETED",
        shelfType = "STORE",
        createdDate = "2024-01-10",
        reason = "Great book!",
        bookInfo = MyBookDetailBookInfo(
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
        ),
        historyInfo = MyBookDetailHistoryInfo(
            startedDate = "2024-01-15",
            finishedDate = "2024-02-01"
        )
    )

    @Test
    fun `init with valid mybookId fetches detail successfully`() = runTest {
        // Given
        val detail = createDetail()
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Success(detail))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))

        // When
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is BookInfoUiState.Success)
        assertEquals(detail, (state as BookInfoUiState.Success).detail)
    }

    @Test
    fun `init with valid mybookId handles error`() = runTest {
        // Given
        val errorMessage = "Failed to load"
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Error(errorMessage))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))

        // When
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is BookInfoUiState.Error)
        assertEquals(errorMessage, (state as BookInfoUiState.Error).message)
    }

    @Test
    fun `init with invalid mybookId sets error state`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()

        // When
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is BookInfoUiState.Error)
    }

    @Test
    fun `updateMyBook success calls useCase and refreshes detail`() = runTest {
        // Given
        val detail = createDetail()
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Success(detail))
        every { updateMyBookUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns flowOf(DataResource.Success(123))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateMyBook(status = "READING")
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getMyBookDetailUseCase(123) } // init + after update
        coVerify { SnackbarManager.show("책 정보를 수정했어요") }
    }

    @Test
    fun `updateMyBook error shows error snackbar`() = runTest {
        // Given
        val detail = createDetail()
        val errorMessage = "Update failed"
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Success(detail))
        every { updateMyBookUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns flowOf(DataResource.Error(errorMessage))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateMyBook(status = "READING")
        advanceUntilIdle()

        // Then
        coVerify { SnackbarManager.show(errorMessage) }
    }

    @Test
    fun `updateMyBook with status and bookInfo passes all params`() = runTest {
        // Given
        val detail = createDetail()
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Success(detail))
        every { updateMyBookUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns flowOf(DataResource.Success(123))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateMyBook(
            status = "READING",
            reason = "Interesting",
            startedDate = "2024-01-01",
            finishedDate = "2024-02-01",
            bookInfoTitle = "New Title",
            bookInfoAuthor = "New Author",
            bookInfoPublisher = "New Publisher",
            bookInfoPublishDate = "2024-01-01",
            bookInfoIsbn = "9876543210",
            bookInfoTotalPage = 400
        )
        advanceUntilIdle()

        // Then
        coVerify {
            updateMyBookUseCase(
                mybookId = 123,
                status = "READING",
                reason = "Interesting",
                startedDate = "2024-01-01",
                finishedDate = "2024-02-01",
                bookInfoTitle = "New Title",
                bookInfoAuthor = "New Author",
                bookInfoPublisher = "New Publisher",
                bookInfoPublishDate = "2024-01-01",
                bookInfoIsbn = "9876543210",
                bookInfoTotalPage = 400
            )
        }
    }

    @Test
    fun `updateMyBook does nothing when mybookId is invalid`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateMyBook(status = "READING")
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { updateMyBookUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `deleteBook success sets deleteSuccess to true`() = runTest {
        // Given
        val detail = createDetail()
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Success(detail))
        coEvery { deleteMyBookUseCase(123) } returns flowOf(DataResource.Success(Unit))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.deleteBook()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.deleteSuccess.value)
        coVerify { SnackbarManager.show("책을 정리했어요.") }
    }

    @Test
    fun `deleteBook error shows error snackbar`() = runTest {
        // Given
        val detail = createDetail()
        val errorMessage = "Delete failed"
        coEvery { getMyBookDetailUseCase(123) } returns flowOf(DataResource.Success(detail))
        coEvery { deleteMyBookUseCase(123) } returns flowOf(DataResource.Error(errorMessage))

        val savedStateHandle = SavedStateHandle(mapOf("mybookId" to 123))
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.deleteBook()
        advanceUntilIdle()

        // Then
        coVerify { SnackbarManager.show(errorMessage) }
    }

    @Test
    fun `deleteBook does nothing when mybookId is invalid`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val viewModel = BookInfoViewModel(savedStateHandle, getMyBookDetailUseCase, deleteMyBookUseCase, updateMyBookUseCase)
        advanceUntilIdle()

        // When
        viewModel.deleteBook()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { deleteMyBookUseCase(any()) }
    }
}
