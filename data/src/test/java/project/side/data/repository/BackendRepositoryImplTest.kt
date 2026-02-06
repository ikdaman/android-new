package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.BackendDataSource
import project.side.data.model.SaveResultEntity
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo

class BackendRepositoryImplTest {

    @MockK
    private lateinit var backendDataSource: BackendDataSource

    private lateinit var repository: BackendRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = BackendRepositoryImpl(backendDataSource)
    }

    @Test
    fun `saveManualBookInfo emits loading then success true when code is 201`() = runTest {
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
        val saveResult = SaveResultEntity(code = 201, message = "Created")
        coEvery { backendDataSource.saveManualBookInfo(any()) } returns saveResult

        // When
        val flow = repository.saveManualBookInfo(manualBookInfo)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        assertEquals(true, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { backendDataSource.saveManualBookInfo(any()) }
    }

    @Test
    fun `saveManualBookInfo emits loading then success false when code is not 201`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "실패 책", author = "테스트")
        val saveResult = SaveResultEntity(code = 400, message = "Bad Request")
        coEvery { backendDataSource.saveManualBookInfo(any()) } returns saveResult

        // When
        val flow = repository.saveManualBookInfo(manualBookInfo)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        assertEquals(false, (results[1] as DataResource.Success).data)
    }

    @Test
    fun `saveManualBookInfo emits loading then error when exception occurs`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "에러 책", author = "저자")
        val errorMessage = "네트워크 오류"
        coEvery { backendDataSource.saveManualBookInfo(any()) } throws RuntimeException(errorMessage)

        // When
        val flow = repository.saveManualBookInfo(manualBookInfo)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `saveManualBookInfo handles null code as failure`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "널 코드", author = "테스트")
        val saveResult = SaveResultEntity(code = null, message = "Unknown")
        coEvery { backendDataSource.saveManualBookInfo(any()) } returns saveResult

        // When
        val flow = repository.saveManualBookInfo(manualBookInfo)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        assertEquals(false, (results[1] as DataResource.Success).data)
    }

    @Test
    fun `saveManualBookInfo handles code 200 as failure`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "200 코드", author = "테스트")
        val saveResult = SaveResultEntity(code = 200, message = "OK but not created")
        coEvery { backendDataSource.saveManualBookInfo(any()) } returns saveResult

        // When
        val flow = repository.saveManualBookInfo(manualBookInfo)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        assertEquals(false, (results[1] as DataResource.Success).data)
    }

    @Test
    fun `saveManualBookInfo emits error with message on exception with null message`() = runTest {
        // Given
        val manualBookInfo = ManualBookInfo(title = "널 메시지", author = "테스트")
        coEvery { backendDataSource.saveManualBookInfo(any()) } throws RuntimeException()

        // When
        val flow = repository.saveManualBookInfo(manualBookInfo)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Error)
        // null message is allowed
    }
}
