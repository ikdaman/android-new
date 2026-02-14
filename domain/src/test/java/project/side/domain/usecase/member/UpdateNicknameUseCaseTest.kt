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
import project.side.domain.model.Member
import project.side.domain.repository.MemberRepository

class UpdateNicknameUseCaseTest {
    @MockK
    private lateinit var memberRepository: MemberRepository
    private lateinit var useCase: UpdateNicknameUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateNicknameUseCase(memberRepository)
    }

    @Test
    fun `invoke returns success flow with updated member`() = runTest {
        // Given
        val newNickname = "updateduser"
        val updatedMember = Member(nickname = newNickname)
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(updatedMember))
        coEvery { memberRepository.updateNickname(newNickname) } returns expectedFlow

        // When
        val results = useCase.invoke(newNickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(updatedMember, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { memberRepository.updateNickname(newNickname) }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val newNickname = "updateduser"
        val errorMessage = "Failed to update nickname"
        val expectedFlow = flowOf(
            DataResource.Loading<Member>(),
            DataResource.Error(errorMessage)
        )
        coEvery { memberRepository.updateNickname(newNickname) } returns expectedFlow

        // When
        val results = useCase.invoke(newNickname).toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { memberRepository.updateNickname(newNickname) }
    }

    @Test
    fun `invoke passes correct nickname to repository`() = runTest {
        // Given
        val newNickname = "customnickname"
        val updatedMember = Member(nickname = newNickname)
        val expectedFlow = flowOf(DataResource.Success(updatedMember))
        coEvery { memberRepository.updateNickname(newNickname) } returns expectedFlow

        // When
        useCase.invoke(newNickname).toList()

        // Then
        coVerify(exactly = 1) { memberRepository.updateNickname(newNickname) }
    }
}
