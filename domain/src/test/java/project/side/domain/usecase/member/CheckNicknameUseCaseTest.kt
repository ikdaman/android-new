package project.side.domain.usecase.member

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
import project.side.domain.model.NicknameCheck
import project.side.domain.repository.MemberRepository

class CheckNicknameUseCaseTest {
    @MockK
    private lateinit var memberRepository: MemberRepository
    private lateinit var useCase: CheckNicknameUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = CheckNicknameUseCase(memberRepository)
    }

    @Test
    fun `invoke returns success flow with available nickname`() = runTest {
        // Given
        val nickname = "availablenick"
        val nicknameCheck = NicknameCheck(available = true)
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(nicknameCheck))
        coEvery { memberRepository.checkNickname(nickname) } returns expectedFlow

        // When
        val results = useCase.invoke(nickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(nicknameCheck, (results[1] as DataResource.Success).data)
        assertEquals(true, (results[1] as DataResource.Success).data?.available)
        coVerify(exactly = 1) { memberRepository.checkNickname(nickname) }
    }

    @Test
    fun `invoke returns success flow with unavailable nickname`() = runTest {
        // Given
        val nickname = "takennick"
        val nicknameCheck = NicknameCheck(available = false)
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(nicknameCheck))
        coEvery { memberRepository.checkNickname(nickname) } returns expectedFlow

        // When
        val results = useCase.invoke(nickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(nicknameCheck, (results[1] as DataResource.Success).data)
        assertEquals(false, (results[1] as DataResource.Success).data?.available)
        coVerify(exactly = 1) { memberRepository.checkNickname(nickname) }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val nickname = "testnick"
        val errorMessage = "Failed to check nickname"
        val expectedFlow = flowOf(
            DataResource.Loading<NicknameCheck>(),
            DataResource.Error(errorMessage)
        )
        coEvery { memberRepository.checkNickname(nickname) } returns expectedFlow

        // When
        val results = useCase.invoke(nickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { memberRepository.checkNickname(nickname) }
    }
}
