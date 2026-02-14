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

class GetMyInfoUseCaseTest {
    @MockK
    private lateinit var memberRepository: MemberRepository
    private lateinit var useCase: GetMyInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetMyInfoUseCase(memberRepository)
    }

    @Test
    fun `invoke returns success flow with member data`() = runTest {
        // Given
        val member = Member(nickname = "testuser")
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(member))
        coEvery { memberRepository.getMyInfo() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        assertEquals(member, (results[1] as DataResource.Success).data)
        coVerify(exactly = 1) { memberRepository.getMyInfo() }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val errorMessage = "Failed to get member info"
        val expectedFlow = flowOf(
            DataResource.Loading<Member>(),
            DataResource.Error(errorMessage)
        )
        coEvery { memberRepository.getMyInfo() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { memberRepository.getMyInfo() }
    }

    @Test
    fun `invoke calls repository getMyInfo`() = runTest {
        // Given
        val member = Member(nickname = "testuser")
        val expectedFlow = flowOf(DataResource.Success(member))
        coEvery { memberRepository.getMyInfo() } returns expectedFlow

        // When
        useCase.invoke().toList()

        // Then
        coVerify(exactly = 1) { memberRepository.getMyInfo() }
    }
}
