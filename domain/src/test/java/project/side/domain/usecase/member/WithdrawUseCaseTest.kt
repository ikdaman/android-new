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
import project.side.domain.repository.MemberRepository

class WithdrawUseCaseTest {
    @MockK
    private lateinit var memberRepository: MemberRepository
    private lateinit var useCase: WithdrawUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = WithdrawUseCase(memberRepository)
    }

    @Test
    fun `invoke returns success flow from repository`() = runTest {
        // Given
        val expectedFlow = flowOf(DataResource.Loading(), DataResource.Success(Unit))
        coEvery { memberRepository.withdraw() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Success)
        coVerify(exactly = 1) { memberRepository.withdraw() }
    }

    @Test
    fun `invoke returns error flow from repository`() = runTest {
        // Given
        val errorMessage = "Failed to withdraw"
        val expectedFlow = flowOf(
            DataResource.Loading<Unit>(),
            DataResource.Error(errorMessage)
        )
        coEvery { memberRepository.withdraw() } returns expectedFlow

        // When
        val results = useCase.invoke().toList()

        // Then
        assertEquals(2, results.size)
        assert(results[0] is DataResource.Loading)
        assert(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
        coVerify(exactly = 1) { memberRepository.withdraw() }
    }

    @Test
    fun `invoke calls repository withdraw`() = runTest {
        // Given
        val expectedFlow = flowOf(DataResource.Success(Unit))
        coEvery { memberRepository.withdraw() } returns expectedFlow

        // When
        useCase.invoke().toList()

        // Then
        coVerify(exactly = 1) { memberRepository.withdraw() }
    }
}
