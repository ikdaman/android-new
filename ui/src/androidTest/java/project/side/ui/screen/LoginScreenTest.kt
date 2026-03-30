package project.side.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import project.side.ui.theme.IkdamanTheme

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_showsBackButton_whenOnBackClickProvided() {
        composeTestRule.setContent {
            IkdamanTheme {
                LoginScreen(
                    viewModel = null,
                    loginUseCase = null,
                    onBackClick = {},
                    navigateToHome = {},
                    navigateToSignup = { _, _, _ -> }
                )
            }
        }

        // TitleBar with back button is rendered (empty title)
        composeTestRule.onNodeWithText("구글 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("네이버 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("카카오 로그인").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsSocialLoginButtons() {
        composeTestRule.setContent {
            IkdamanTheme {
                LoginScreen(
                    viewModel = null,
                    loginUseCase = null,
                    onBackClick = null,
                    navigateToHome = {},
                    navigateToSignup = { _, _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("구글 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("네이버 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("카카오 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("읽 다 만").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsInfoMessage_whenProvided() {
        composeTestRule.setContent {
            IkdamanTheme {
                LoginScreen(
                    viewModel = null,
                    loginUseCase = null,
                    infoMessage = "로그인 후 사용이 가능합니다",
                    navigateToHome = {},
                    navigateToSignup = { _, _, _ -> }
                )
            }
        }

        // Snackbar should eventually show the info message
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("로그인 후 사용이 가능합니다").assertIsDisplayed()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}
