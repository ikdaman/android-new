package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
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
import project.side.domain.model.NicknameCheck
import project.side.domain.model.SignupState
import project.side.domain.usecase.SignupUseCase
import project.side.domain.usecase.member.CheckNicknameUseCase
import project.side.presentation.model.SignupUIState

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {
    @MockK
    private lateinit var signupUseCase: SignupUseCase

    @MockK
    private lateinit var checkNicknameUseCase: CheckNicknameUseCase

    private lateinit var viewModel: SignupViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = SignupViewModel(checkNicknameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Init`() {
        // Then
        assertEquals(SignupUIState.Init, viewModel.uiState.value)
    }

    @Test
    fun `checkNickname emits NicknameChecked with available true`() = runTest {
        // Given
        val nickname = "availablenick"
        val nicknameCheck = NicknameCheck(available = true)
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.loading<NicknameCheck>(),
            DataResource.success(nicknameCheck)
        )

        // When
        viewModel.checkNickname(nickname)

        // Then
        assertEquals(SignupUIState.NicknameChecked(true), viewModel.uiState.value)
        coVerify(exactly = 1) { checkNicknameUseCase(nickname) }
    }

    @Test
    fun `checkNickname emits NicknameChecked with available false`() = runTest {
        // Given
        val nickname = "takennick"
        val nicknameCheck = NicknameCheck(available = false)
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.loading<NicknameCheck>(),
            DataResource.success(nicknameCheck)
        )

        // When
        viewModel.checkNickname(nickname)

        // Then
        assertEquals(SignupUIState.NicknameChecked(false), viewModel.uiState.value)
        coVerify(exactly = 1) { checkNicknameUseCase(nickname) }
    }

    @Test
    fun `checkNickname emits Error on failure`() = runTest {
        // Given
        val nickname = "testnick"
        val errorMessage = "Failed to check nickname"
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.loading<NicknameCheck>(),
            DataResource.error(errorMessage)
        )

        // When
        viewModel.checkNickname(nickname)

        // Then
        assertEquals(SignupUIState.Error(errorMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { checkNicknameUseCase(nickname) }
    }

    @Test
    fun `signup emits Success on success`() = runTest {
        // Given
        val socialToken = "token123"
        val provider = "GOOGLE"
        val providerId = "google123"
        val nickname = "testnick"
        val expectedFlow = flowOf(
            SignupState.Loading,
            SignupState.Success
        )
        coEvery { signupUseCase(socialToken, provider, providerId, nickname) } returns expectedFlow

        // When
        viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)

        // Then
        assertEquals(SignupUIState.Success, viewModel.uiState.value)
        coVerify(exactly = 1) { signupUseCase(socialToken, provider, providerId, nickname) }
    }

    @Test
    fun `signup emits Error on failure`() = runTest {
        // Given
        val socialToken = "token123"
        val provider = "GOOGLE"
        val providerId = "google123"
        val nickname = "testnick"
        val errorMessage = "Signup failed"
        val expectedFlow = flowOf(
            SignupState.Loading,
            SignupState.Error(errorMessage)
        )
        coEvery { signupUseCase(socialToken, provider, providerId, nickname) } returns expectedFlow

        // When
        viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)

        // Then
        assertEquals(SignupUIState.Error(errorMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { signupUseCase(socialToken, provider, providerId, nickname) }
    }
}
