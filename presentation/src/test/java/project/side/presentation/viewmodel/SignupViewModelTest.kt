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
    fun `signup checks nickname then signs up on available`() = runTest {
        // Given
        val socialToken = "token123"
        val provider = "GOOGLE"
        val providerId = "google123"
        val nickname = "testnick"
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.success(NicknameCheck(available = true))
        )
        coEvery { signupUseCase(socialToken, provider, providerId, nickname) } returns flowOf(
            SignupState.Loading,
            SignupState.Success
        )

        // When
        viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)

        // Then
        assertEquals(SignupUIState.Success, viewModel.uiState.value)
        coVerify(exactly = 1) { checkNicknameUseCase(nickname) }
        coVerify(exactly = 1) { signupUseCase(socialToken, provider, providerId, nickname) }
    }

    @Test
    fun `signup emits NicknameDuplicate when nickname is taken`() = runTest {
        // Given
        val socialToken = "token123"
        val provider = "GOOGLE"
        val providerId = "google123"
        val nickname = "takennick"
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.success(NicknameCheck(available = false))
        )

        // When
        viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)

        // Then
        assertEquals(SignupUIState.NicknameDuplicate, viewModel.uiState.value)
        coVerify(exactly = 1) { checkNicknameUseCase(nickname) }
        coVerify(exactly = 0) { signupUseCase(any(), any(), any(), any()) }
    }

    @Test
    fun `signup emits Error on signup failure`() = runTest {
        // Given
        val socialToken = "token123"
        val provider = "GOOGLE"
        val providerId = "google123"
        val nickname = "testnick"
        val errorMessage = "Signup failed"
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.success(NicknameCheck(available = true))
        )
        coEvery { signupUseCase(socialToken, provider, providerId, nickname) } returns flowOf(
            SignupState.Loading,
            SignupState.Error(errorMessage)
        )

        // When
        viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)

        // Then
        assertEquals(SignupUIState.Error(errorMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { checkNicknameUseCase(nickname) }
        coVerify(exactly = 1) { signupUseCase(socialToken, provider, providerId, nickname) }
    }

    @Test
    fun `signup emits Error on nickname check failure`() = runTest {
        // Given
        val socialToken = "token123"
        val provider = "GOOGLE"
        val providerId = "google123"
        val nickname = "testnick"
        coEvery { checkNicknameUseCase(nickname) } returns flowOf(
            DataResource.error("닉네임 확인 중 오류가 발생했습니다")
        )

        // When
        viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)

        // Then
        assertEquals(SignupUIState.Error("닉네임 확인 중 오류가 발생했습니다"), viewModel.uiState.value)
        coVerify(exactly = 0) { signupUseCase(any(), any(), any(), any()) }
    }
}
