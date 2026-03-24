package project.side.domain.usecase

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
import project.side.domain.model.ManualBookInfo
import project.side.domain.repository.MyBookRepository

class SaveManualBookInfoUseCaseTest {
    @MockK
    private lateinit var myBookRepository: MyBookRepository
    private lateinit var useCase: SaveManualBookInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SaveManualBookInfoUseCase(myBookRepository)
    }

    @Test
    fun `invoke returns success flow from repository`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "테스트 책", author = "테스트 저자")
        val savedId = 42
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(savedId))
        coEvery { myBookRepository.saveMyBook(manualBookInfo) } returns expectedFlow

        // When
        val results = useCase.invoke(manualBookInfo).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(savedId, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { myBookRepository.saveMyBook(manualBookInfo) }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "테스트 책", author = "테스트 저자")
        val errorMessage = "Failed to save book"
        val expectedFlow = flowOf(
            DataResource.Loading<Int>(),
            DataResource.Error(errorMessage)
        )
        coEvery { myBookRepository.saveMyBook(manualBookInfo) } returns expectedFlow

        // When
        val results = useCase.invoke(manualBookInfo).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { myBookRepository.saveMyBook(manualBookInfo) }
    }

    @Test
    fun `invoke passes correct ManualBookInfo to repository`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(
            title = "직접 입력 도서",
            author = "저자명",
            publisher = "출판사",
            pageCount = 300,
            isbn = "978-3-16-148410-0"
        )
        coEvery { myBookRepository.saveMyBook(manualBookInfo) } returns flowOf(DataResource.Success(1))

        // When
        useCase.invoke(manualBookInfo).toList()

        // Then
        coVerify(exactly = 1) { myBookRepository.saveMyBook(manualBookInfo) }
    }

    @Test
    fun `invoke returns loading then success for minimal book info`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo()
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(0))
        coEvery { myBookRepository.saveMyBook(manualBookInfo) } returns expectedFlow

        // When
        val results = useCase.invoke(manualBookInfo).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        coVerify(exactly = 1) { myBookRepository.saveMyBook(manualBookInfo) }
    }
}
