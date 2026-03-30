package project.side.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import project.side.ui.theme.IkdamanTheme

class ManualBookInputScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun manualBookInputScreen_displaysInputFields() {
        composeTestRule.setContent {
            IkdamanTheme {
                ManualBookInputScreen(
                    appNavController = rememberNavController(),
                    viewModel = null
                )
            }
        }

        composeTestRule.onNodeWithText("제목").assertIsDisplayed()
        composeTestRule.onNodeWithText("작가").assertIsDisplayed()
        composeTestRule.onNodeWithText("출판사").assertIsDisplayed()
        composeTestRule.onNodeWithText("직접 입력").assertIsDisplayed()
    }

    @Test
    fun manualBookInputScreen_saveButton_callsLoginRequired_whenNotLoggedIn() {
        var loginRequested = false

        composeTestRule.setContent {
            IkdamanTheme {
                ManualBookInputScreen(
                    appNavController = rememberNavController(),
                    viewModel = null,
                    isLoggedIn = false,
                    onLoginRequired = { loginRequested = true }
                )
            }
        }

        composeTestRule.onNodeWithText("저장").performClick()
        assertTrue("비로그인 시 저장 클릭하면 onLoginRequired 호출", loginRequested)
    }
}
