package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.every
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.domain.model.LoginState
import project.side.domain.model.SocialAuthType
import project.side.domain.usecase.auth.LoginUseCase
import project.side.presentation.model.LoginUIState

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @MockK
    private lateinit var loginUseCase: LoginUseCase

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial uiState is Init`() = runTest {
        assertEquals(LoginUIState.Init, viewModel.uiState.value)
    }

    // ── googleLogin ────────────────────────────────────────────────────────────

    @Test
    fun `googleLogin success sets uiState to Success`() = runTest {
        // Given
        every { loginUseCase(SocialAuthType.GOOGLE) } returns flowOf(
            LoginState.Loading,
            LoginState.Success
        )

        // When
        viewModel.googleLogin()

        // Then
        assertEquals(LoginUIState.Success("로그인 성공"), viewModel.uiState.value)
    }

    @Test
    fun `googleLogin loading sets uiState to Loading`() = runTest {
        // Given
        every { loginUseCase(SocialAuthType.GOOGLE) } returns flowOf(LoginState.Loading)

        // When
        viewModel.googleLogin()

        // Then
        assertEquals(LoginUIState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `googleLogin error sets uiState to Error`() = runTest {
        // Given
        val errorMessage = "구글 로그인 실패"
        every { loginUseCase(SocialAuthType.GOOGLE) } returns flowOf(
            LoginState.Error(errorMessage)
        )

        // When
        viewModel.googleLogin()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUIState.Error)
        assertEquals(errorMessage, (state as LoginUIState.Error).message)
    }

    @Test
    fun `googleLogin signupRequired sets uiState to SignupRequired`() = runTest {
        // Given
        every { loginUseCase(SocialAuthType.GOOGLE) } returns flowOf(
            LoginState.SignupRequired(
                socialToken = "token-abc",
                provider = "GOOGLE",
                providerId = "12345"
            )
        )

        // When
        viewModel.googleLogin()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUIState.SignupRequired)
        with(state as LoginUIState.SignupRequired) {
            assertEquals("token-abc", socialToken)
            assertEquals("GOOGLE", provider)
            assertEquals("12345", providerId)
        }
    }

    // ── naverLogin ─────────────────────────────────────────────────────────────

    @Test
    fun `naverLogin success sets uiState to Success`() = runTest {
        // Given
        every { loginUseCase(SocialAuthType.NAVER) } returns flowOf(LoginState.Success)

        // When
        viewModel.naverLogin()

        // Then
        assertEquals(LoginUIState.Success("로그인 성공"), viewModel.uiState.value)
    }

    @Test
    fun `naverLogin error sets uiState to Error`() = runTest {
        // Given
        val errorMessage = "네이버 로그인 실패"
        every { loginUseCase(SocialAuthType.NAVER) } returns flowOf(LoginState.Error(errorMessage))

        // When
        viewModel.naverLogin()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUIState.Error)
        assertEquals(errorMessage, (state as LoginUIState.Error).message)
    }

    // ── kakaoLogin ─────────────────────────────────────────────────────────────

    @Test
    fun `kakaoLogin success sets uiState to Success`() = runTest {
        // Given
        every { loginUseCase(SocialAuthType.KAKAO) } returns flowOf(LoginState.Success)

        // When
        viewModel.kakaoLogin()

        // Then
        assertEquals(LoginUIState.Success("로그인 성공"), viewModel.uiState.value)
    }

    @Test
    fun `kakaoLogin error sets uiState to Error`() = runTest {
        // Given
        val errorMessage = "카카오 로그인 실패"
        every { loginUseCase(SocialAuthType.KAKAO) } returns flowOf(LoginState.Error(errorMessage))

        // When
        viewModel.kakaoLogin()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUIState.Error)
        assertEquals(errorMessage, (state as LoginUIState.Error).message)
    }

    // ── resetState ─────────────────────────────────────────────────────────────

    @Test
    fun `resetState sets uiState back to Init`() = runTest {
        // Given
        every { loginUseCase(SocialAuthType.GOOGLE) } returns flowOf(LoginState.Success)
        viewModel.googleLogin()

        // When
        viewModel.resetState()

        // Then
        assertEquals(LoginUIState.Init, viewModel.uiState.value)
    }
}
