package project.side.presentation.viewmodel

import app.cash.turbine.test
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
import project.side.domain.model.ManualBookInfo
import project.side.domain.usecase.SaveManualBookInfoUseCase
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ManualInputViewModelTest {

    @MockK
    private lateinit var saveManualBookInfoUseCase: SaveManualBookInfoUseCase

    private lateinit var viewModel: ManualInputViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = ManualInputViewModel(saveManualBookInfoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── saveManualBookInfo ─────────────────────────────────────────────────────

    @Test
    fun `saveManualBookInfo success emits SaveEvent Success`() = runTest {
        // Given
        val manual = ManualBookInfo(title = "테스트 책", author = "저자")
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveManualBookInfo(manual)
            assertEquals(SaveEvent.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveManualBookInfo error emits SaveEvent Error with message`() = runTest {
        // Given
        val manual = ManualBookInfo(title = "테스트 책", author = "저자")
        val errorMessage = "서버 오류"
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.error(errorMessage))

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveManualBookInfo(manual)
            val event = awaitItem()
            assertTrue(event is SaveEvent.Error)
            assertEquals(errorMessage, (event as SaveEvent.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveManualBookInfo error with null message emits default error message`() = runTest {
        // Given
        val manual = ManualBookInfo(title = "테스트 책", author = "저자")
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.error(null))

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveManualBookInfo(manual)
            val event = awaitItem()
            assertTrue(event is SaveEvent.Error)
            assertEquals("책 저장에 실패했어요.", (event as SaveEvent.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveManualBookInfo loading does not emit any event`() = runTest {
        // Given
        val manual = ManualBookInfo(title = "테스트 책", author = "저자")
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.loading())

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveManualBookInfo(manual)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveManualBookInfo calls useCase with the provided ManualBookInfo`() = runTest {
        // Given
        val manual = ManualBookInfo(
            source = "CUSTOM",
            title = "책 제목",
            author = "저자명",
            publisher = "출판사",
            pageCount = 300
        )
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))

        // When
        viewModel.saveManualBookInfo(manual)

        // Then
        verify { saveManualBookInfoUseCase(manual) }
    }

    // ── saveManualBookInfoFromUi ───────────────────────────────────────────────

    @Test
    fun `saveManualBookInfoFromUi with minimal fields calls useCase with source CUSTOM`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))

        // When
        viewModel.saveManualBookInfoFromUi(title = "책 제목", author = "저자명", publisher = null, pubDate = null, isbn = null, pageCount = null)

        // Then
        verify {
            saveManualBookInfoUseCase(
                match { it.source == "CUSTOM" && it.title == "책 제목" && it.author == "저자명" }
            )
        }
    }

    @Test
    fun `saveManualBookInfoFromUi with pageCount string converts to Int`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))

        // When
        viewModel.saveManualBookInfoFromUi(
            title = "책",
            author = "저자",
            publisher = null,
            pubDate = null,
            isbn = null,
            pageCount = "250"
        )

        // Then
        verify { saveManualBookInfoUseCase(match { it.pageCount == 250 }) }
    }

    @Test
    fun `saveManualBookInfoFromUi with invalid pageCount string sets pageCount to null`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))

        // When
        viewModel.saveManualBookInfoFromUi(
            title = "책",
            author = "저자",
            publisher = null,
            pubDate = null,
            isbn = null,
            pageCount = "not-a-number"
        )

        // Then
        verify { saveManualBookInfoUseCase(match { it.pageCount == null }) }
    }

    @Test
    fun `saveManualBookInfoFromUi with startDate formats to UTC ISO string`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))
        val startDate = LocalDate.of(2024, 3, 15)

        // When
        viewModel.saveManualBookInfoFromUi(
            title = "책",
            author = "저자",
            publisher = null,
            pubDate = null,
            isbn = null,
            pageCount = null,
            startDate = startDate
        )

        // Then
        verify {
            saveManualBookInfoUseCase(
                match { it.startDate == "2024-03-15T00:00:00Z" }
            )
        }
    }

    @Test
    fun `saveManualBookInfoFromUi with endDate formats to UTC ISO string`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))
        val endDate = LocalDate.of(2024, 6, 1)

        // When
        viewModel.saveManualBookInfoFromUi(
            title = "책",
            author = "저자",
            publisher = null,
            pubDate = null,
            isbn = null,
            pageCount = null,
            endDate = endDate
        )

        // Then
        verify {
            saveManualBookInfoUseCase(
                match { it.endDate == "2024-06-01T00:00:00Z" }
            )
        }
    }

    @Test
    fun `saveManualBookInfoFromUi success emits SaveEvent Success`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.success(1))

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveManualBookInfoFromUi(
                title = "책",
                author = "저자",
                publisher = null,
                pubDate = null,
                isbn = null,
                pageCount = null
            )
            assertEquals(SaveEvent.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveManualBookInfoFromUi error emits SaveEvent Error`() = runTest {
        // Given
        every { saveManualBookInfoUseCase(any()) } returns flowOf(DataResource.error("저장 실패"))

        // When / Then
        viewModel.saveEvent.test {
            viewModel.saveManualBookInfoFromUi(
                title = "책",
                author = "저자",
                publisher = null,
                pubDate = null,
                isbn = null,
                pageCount = null
            )
            val event = awaitItem()
            assertTrue(event is SaveEvent.Error)
            assertEquals("저장 실패", (event as SaveEvent.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
