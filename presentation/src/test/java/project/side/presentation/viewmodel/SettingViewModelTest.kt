package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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
import project.side.domain.model.LogoutState
import project.side.domain.model.Member
import project.side.domain.model.SocialAuthType
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import project.side.domain.usecase.member.UpdateNicknameUseCase
import project.side.presentation.model.SettingUIState

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {
    @MockK
    private lateinit var logoutUseCase: LogoutUseCase

    @MockK
    private lateinit var getProviderUseCase: GetProviderUseCase

    @MockK
    private lateinit var getMyInfoUseCase: GetMyInfoUseCase

    @MockK
    private lateinit var updateNicknameUseCase: UpdateNicknameUseCase

    private lateinit var viewModel: SettingViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        every { getMyInfoUseCase() } returns flowOf(
            DataResource.Success(Member(nickname = "테스트"))
        )
        viewModel = SettingViewModel(getMyInfoUseCase, updateNicknameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Init`() {
        assertEquals(SettingUIState.Init, viewModel.uiState.value)
    }

    @Test
    fun `logout with GOOGLE provider emits LogoutSuccess`() = runTest {
        // Given
        coEvery { getProviderUseCase() } returns "GOOGLE"
        every { logoutUseCase(SocialAuthType.GOOGLE) } returns flowOf(
            LogoutState.Loading,
            LogoutState.Success
        )

        // When
        viewModel.logout(logoutUseCase, getProviderUseCase)

        // Then
        assertEquals(SettingUIState.LogoutSuccess, viewModel.uiState.value)
        coVerify(exactly = 1) { getProviderUseCase() }
        verify(exactly = 1) { logoutUseCase(SocialAuthType.GOOGLE) }
    }

    @Test
    fun `logout with NAVER provider emits LogoutSuccess`() = runTest {
        // Given
        coEvery { getProviderUseCase() } returns "NAVER"
        every { logoutUseCase(SocialAuthType.NAVER) } returns flowOf(
            LogoutState.Loading,
            LogoutState.Success
        )

        // When
        viewModel.logout(logoutUseCase, getProviderUseCase)

        // Then
        assertEquals(SettingUIState.LogoutSuccess, viewModel.uiState.value)
        coVerify(exactly = 1) { getProviderUseCase() }
        verify(exactly = 1) { logoutUseCase(SocialAuthType.NAVER) }
    }

    @Test
    fun `logout with KAKAO provider emits LogoutSuccess`() = runTest {
        // Given
        coEvery { getProviderUseCase() } returns "KAKAO"
        every { logoutUseCase(SocialAuthType.KAKAO) } returns flowOf(
            LogoutState.Loading,
            LogoutState.Success
        )

        // When
        viewModel.logout(logoutUseCase, getProviderUseCase)

        // Then
        assertEquals(SettingUIState.LogoutSuccess, viewModel.uiState.value)
        coVerify(exactly = 1) { getProviderUseCase() }
        verify(exactly = 1) { logoutUseCase(SocialAuthType.KAKAO) }
    }

    @Test
    fun `logout with null provider emits Error`() = runTest {
        // Given
        coEvery { getProviderUseCase() } returns null

        // When
        viewModel.logout(logoutUseCase, getProviderUseCase)

        // Then
        assertEquals(SettingUIState.Error("로그인 정보를 찾을 수 없습니다."), viewModel.uiState.value)
        coVerify(exactly = 1) { getProviderUseCase() }
    }

    @Test
    fun `logout with unknown provider emits Error`() = runTest {
        // Given
        coEvery { getProviderUseCase() } returns "UNKNOWN"

        // When
        viewModel.logout(logoutUseCase, getProviderUseCase)

        // Then
        assertEquals(SettingUIState.Error("로그인 정보를 찾을 수 없습니다."), viewModel.uiState.value)
        coVerify(exactly = 1) { getProviderUseCase() }
    }

    @Test
    fun `logout emits Error when logout fails`() = runTest {
        // Given
        val errorMessage = "로그아웃 실패"
        coEvery { getProviderUseCase() } returns "GOOGLE"
        every { logoutUseCase(SocialAuthType.GOOGLE) } returns flowOf(
            LogoutState.Loading,
            LogoutState.Error(errorMessage)
        )

        // When
        viewModel.logout(logoutUseCase, getProviderUseCase)

        // Then
        assertEquals(SettingUIState.Error(errorMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { getProviderUseCase() }
        verify(exactly = 1) { logoutUseCase(SocialAuthType.GOOGLE) }
    }
}
