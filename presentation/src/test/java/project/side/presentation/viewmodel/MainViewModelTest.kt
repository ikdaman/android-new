package project.side.presentation.viewmodel

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
import org.junit.Before
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.Member
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @MockK
    private lateinit var getLoginStateUseCase: GetLoginStateUseCase

    @MockK
    private lateinit var getMyInfoUseCase: GetMyInfoUseCase

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when logged in, validates token by calling getMyInfo`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(true)
        every { getMyInfoUseCase() } returns flowOf(
            DataResource.success(Member(nickname = "test"))
        )

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase)

        // Then
        verify(exactly = 1) { getMyInfoUseCase() }
    }

    @Test
    fun `when logged in, nickname is set from getMyInfo response`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(true)
        every { getMyInfoUseCase() } returns flowOf(
            DataResource.success(Member(nickname = "홍길동"))
        )

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase)

        // Then
        assertEquals("홍길동", viewModel.nickname.value)
    }

    @Test
    fun `when not logged in, does not call getMyInfo`() = runTest {
        // Given
        every { getLoginStateUseCase() } returns flowOf(false)

        // When
        viewModel = MainViewModel(getLoginStateUseCase, getMyInfoUseCase)

        // Then
        verify(exactly = 0) { getMyInfoUseCase() }
    }
}
