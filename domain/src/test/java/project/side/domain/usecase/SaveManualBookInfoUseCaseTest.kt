package project.side.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo
import project.side.domain.repository.BackendRepository

class SaveManualBookInfoUseCaseTest {

    @MockK
    private lateinit var backendRepository: BackendRepository

    private lateinit var useCase: SaveManualBookInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SaveManualBookInfoUseCase(backendRepository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(
            title = "테스트 책",
            author = "홍길동",
            publisher = "테스트출판사",
            isbn = "1234567890",
            reason = "재미있어서",
            startDate = "2024-01-01",
            endDate = "2024-01-31"
        )
        val expectedFlow = flowOf(
            DataResource.loading(),
            DataResource.success(true)
        )
        every { backendRepository.saveManualBookInfo(manualBookInfo) } returns expectedFlow

        // When
        val resultFlow = useCase.invoke(manualBookInfo)
        val results = resultFlow.toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(true, (results[1] as DataResource.Success).data)
        verify(exactly = 1) { backendRepository.saveManualBookInfo(manualBookInfo) }
    }

    @Test
    fun `invoke returns error flow when repository fails`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(
            title = "실패 책",
            author = "테스트"
        )
        val errorMessage = "네트워크 오류"
        val expectedFlow = flowOf(
            DataResource.loading(),
            DataResource.error(errorMessage)
        )
        every { backendRepository.saveManualBookInfo(manualBookInfo) } returns expectedFlow

        // When
        val resultFlow = useCase.invoke(manualBookInfo)
        val results = resultFlow.toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `invoke returns false when save fails with non-201 code`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "실패 책", author = "저자")
        val expectedFlow = flowOf(
            DataResource.loading(),
            DataResource.success(false)
        )
        every { backendRepository.saveManualBookInfo(manualBookInfo) } returns expectedFlow

        // When
        val resultFlow = useCase.invoke(manualBookInfo)
        val results = resultFlow.toList()

        // Then
        assertEquals(2, results.size)
        assert(results[1] is DataResource.Success)
        assertEquals(false, (results[1] as DataResource.Success).data)
    }
}
