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
import project.side.data.datasource.MemberDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.MemberEntity
import project.side.data.model.NicknameCheckEntity
import project.side.domain.DataResource

class MemberRepositoryImplTest {

    @MockK
    private lateinit var memberDataSource: MemberDataSource

    private lateinit var repository: MemberRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = MemberRepositoryImpl(memberDataSource)
    }

    // getMyInfo tests
    @Test
    fun `getMyInfo emits loading then success when data source succeeds`() = runTest {
        // Given
        val memberEntity = MemberEntity(nickname = "테스트유저")
        coEvery { memberDataSource.getMyInfo() } returns DataApiResult.Success(memberEntity)

        // When
        val flow = repository.getMyInfo()
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals("테스트유저", successData.nickname)
        coVerify(exactly = 1) { memberDataSource.getMyInfo() }
    }

    @Test
    fun `getMyInfo emits loading then error when data source returns error`() = runTest {
        // Given
        val errorMessage = "회원 정보를 불러올 수 없습니다"
        coEvery { memberDataSource.getMyInfo() } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.getMyInfo()
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `getMyInfo emits loading then error when exception occurs`() = runTest {
        // Given
        val exceptionMessage = "네트워크 오류"
        coEvery { memberDataSource.getMyInfo() } throws RuntimeException(exceptionMessage)

        // When
        val flow = repository.getMyInfo()
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(exceptionMessage, (results[1] as DataResource.Error).message)
    }

    // updateNickname tests
    @Test
    fun `updateNickname emits loading then success with updated member`() = runTest {
        // Given
        val newNickname = "새닉네임"
        val memberEntity = MemberEntity(nickname = newNickname)
        coEvery { memberDataSource.updateNickname(newNickname) } returns DataApiResult.Success(memberEntity)

        // When
        val flow = repository.updateNickname(newNickname)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(newNickname, successData.nickname)
        coVerify(exactly = 1) { memberDataSource.updateNickname(newNickname) }
    }

    @Test
    fun `updateNickname emits loading then error when data source returns error`() = runTest {
        // Given
        val nickname = "잘못된닉네임"
        val errorMessage = "닉네임 변경 실패"
        coEvery { memberDataSource.updateNickname(nickname) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.updateNickname(nickname)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `updateNickname passes correct nickname to data source`() = runTest {
        // Given
        val nickname = "정확한닉네임"
        val memberEntity = MemberEntity(nickname = nickname)
        coEvery { memberDataSource.updateNickname(nickname) } returns DataApiResult.Success(memberEntity)

        // When
        repository.updateNickname(nickname).toList()

        // Then
        coVerify(exactly = 1) { memberDataSource.updateNickname(nickname) }
    }

    // withdraw tests
    @Test
    fun `withdraw emits loading then success when data source succeeds`() = runTest {
        // Given
        coEvery { memberDataSource.withdraw() } returns DataApiResult.Success(Unit)

        // When
        val flow = repository.withdraw()
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        coVerify(exactly = 1) { memberDataSource.withdraw() }
    }

    @Test
    fun `withdraw emits loading then error when data source returns error`() = runTest {
        // Given
        val errorMessage = "회원 탈퇴 실패"
        coEvery { memberDataSource.withdraw() } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.withdraw()
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `withdraw emits loading then error when exception occurs`() = runTest {
        // Given
        val exceptionMessage = "서버 오류"
        coEvery { memberDataSource.withdraw() } throws RuntimeException(exceptionMessage)

        // When
        val flow = repository.withdraw()
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(exceptionMessage, (results[1] as DataResource.Error).message)
    }

    // checkNickname tests
    @Test
    fun `checkNickname emits loading then success with available true`() = runTest {
        // Given
        val nickname = "사용가능"
        val nicknameCheckEntity = NicknameCheckEntity(available = true)
        coEvery { memberDataSource.checkNickname(nickname) } returns DataApiResult.Success(nicknameCheckEntity)

        // When
        val flow = repository.checkNickname(nickname)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(true, successData.available)
        coVerify(exactly = 1) { memberDataSource.checkNickname(nickname) }
    }

    @Test
    fun `checkNickname emits loading then success with available false`() = runTest {
        // Given
        val nickname = "중복된닉네임"
        val nicknameCheckEntity = NicknameCheckEntity(available = false)
        coEvery { memberDataSource.checkNickname(nickname) } returns DataApiResult.Success(nicknameCheckEntity)

        // When
        val flow = repository.checkNickname(nickname)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(false, successData.available)
    }

    @Test
    fun `checkNickname emits loading then error when data source returns error`() = runTest {
        // Given
        val nickname = "오류닉네임"
        val errorMessage = "닉네임 확인 실패"
        coEvery { memberDataSource.checkNickname(nickname) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.checkNickname(nickname)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }
}
