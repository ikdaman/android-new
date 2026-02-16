package project.side.presentation.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.Runs
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
import project.side.presentation.util.SnackbarManager

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

    @Test
    fun `init fetches nickname from getMyInfoUseCase`() = runTest {
        // Then
        assertEquals("테스트", viewModel.nickname.value)
    }

    @Test
    fun `validateNickname returns true for valid Korean nickname`() {
        // When
        val result = viewModel.validateNickname("홍길동")

        // Then
        assertEquals(true, result)
        assertEquals(null, viewModel.nicknameError.value)
    }

    @Test
    fun `validateNickname returns true for valid alphanumeric`() {
        // When
        val result = viewModel.validateNickname("test123")

        // Then
        assertEquals(true, result)
        assertEquals(null, viewModel.nicknameError.value)
    }

    @Test
    fun `validateNickname returns false for over 10 chars`() {
        // When
        val result = viewModel.validateNickname("12345678901")

        // Then
        assertEquals(false, result)
        assertEquals("닉네임은 최대 10자까지 가능합니다", viewModel.nicknameError.value)
    }

    @Test
    fun `validateNickname returns false for special chars`() {
        // When
        val result = viewModel.validateNickname("test!@#")

        // Then
        assertEquals(false, result)
        assertEquals("한글, 영어, 숫자만 사용할 수 있습니다", viewModel.nicknameError.value)
    }

    @Test
    fun `validateNickname clears error for valid input`() {
        // Given
        viewModel.validateNickname("test!@#") // Set error first

        // When
        val result = viewModel.validateNickname("validName")

        // Then
        assertEquals(true, result)
        assertEquals(null, viewModel.nicknameError.value)
    }

    @Test
    fun `startEditingNickname sets isEditingNickname true and clears error`() {
        // Given
        viewModel.validateNickname("test!@#") // Set error first

        // When
        viewModel.startEditingNickname()

        // Then
        assertEquals(true, viewModel.isEditingNickname.value)
        assertEquals(null, viewModel.nicknameError.value)
    }

    @Test
    fun `cancelEditingNickname sets isEditingNickname false and refetches nickname`() {
        // Given
        viewModel.startEditingNickname()

        // When
        viewModel.cancelEditingNickname()

        // Then
        assertEquals(false, viewModel.isEditingNickname.value)
        assertEquals("테스트", viewModel.nickname.value)
    }

    @Test
    fun `updateNickname success updates nickname and stops editing`() = runTest {
        // Given
        mockkObject(SnackbarManager)
        coEvery { SnackbarManager.show(any()) } just Runs
        every { updateNicknameUseCase("새닉네임") } returns flowOf(
            DataResource.Success(Member(nickname = "새닉네임"))
        )
        viewModel.startEditingNickname()

        // When
        viewModel.updateNickname("새닉네임")

        // Then
        assertEquals("새닉네임", viewModel.nickname.value)
        assertEquals(false, viewModel.isEditingNickname.value)
        assertEquals(null, viewModel.nicknameError.value)
        coVerify(exactly = 1) { SnackbarManager.show("닉네임이 변경되었어요") }
    }

    @Test
    fun `updateNickname error sets nicknameError`() = runTest {
        // Given
        every { updateNicknameUseCase("중복닉네임") } returns flowOf(
            DataResource.Error("중복된 닉네임")
        )

        // When
        viewModel.updateNickname("중복닉네임")

        // Then
        assertEquals("중복된 닉네임", viewModel.nicknameError.value)
    }

    @Test
    fun `updateNickname with blank does not call useCase`() = runTest {
        // When
        viewModel.updateNickname("")

        // Then
        assertEquals("닉네임을 입력해주세요", viewModel.nicknameError.value)
        verify(exactly = 0) { updateNicknameUseCase(any()) }
    }

    @Test
    fun `updateNickname with invalid input does not call useCase`() = runTest {
        // When
        viewModel.updateNickname("test!@#")

        // Then
        assertEquals("한글, 영어, 숫자만 사용할 수 있습니다", viewModel.nicknameError.value)
        verify(exactly = 0) { updateNicknameUseCase(any()) }
    }
}
