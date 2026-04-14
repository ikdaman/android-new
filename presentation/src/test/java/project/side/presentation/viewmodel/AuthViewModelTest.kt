package project.side.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.domain.model.DomainAuthEvent
import project.side.domain.usecase.GetAuthEventUseCase
import project.side.domain.usecase.GetLoginStateUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @MockK
    private lateinit var getAuthEventUseCase: GetAuthEventUseCase

    @MockK
    private lateinit var getLoginStateUseCase: GetLoginStateUseCase

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
    fun `initial isLoggedIn is false`() = runTest {
        // Given
        every { getAuthEventUseCase() } returns flowOf()
        every { getLoginStateUseCase() } returns flowOf(false)

        // When
        val viewModel = AuthViewModel(getAuthEventUseCase, getLoginStateUseCase)

        // Then
        assertFalse(viewModel.isLoggedIn.value)
    }

    @Test
    fun `isLoggedIn reflects login state changes`() = runTest {
        // Given
        every { getAuthEventUseCase() } returns flowOf()
        every { getLoginStateUseCase() } returns flowOf(true)

        // When
        val viewModel = AuthViewModel(getAuthEventUseCase, getLoginStateUseCase)

        // Then
        assertTrue(viewModel.isLoggedIn.value)
    }

    @Test
    fun `authEvent emits LOGIN_REQUIRED when auth event fires`() = runTest {
        // Given
        val authEventFlow = MutableSharedFlow<DomainAuthEvent>()
        every { getAuthEventUseCase() } returns authEventFlow
        every { getLoginStateUseCase() } returns flowOf(false)

        val viewModel = AuthViewModel(getAuthEventUseCase, getLoginStateUseCase)

        // When / Then
        viewModel.authEvent.test {
            authEventFlow.emit(DomainAuthEvent.LOGIN_REQUIRED)
            assertEquals(DomainAuthEvent.LOGIN_REQUIRED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
