package project.side.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import project.side.ui.theme.IkdamanTheme

class SplashScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splashScreen_alwaysNavigatesToHome() {
        var navigated = false

        composeTestRule.setContent {
            IkdamanTheme {
                SplashScreen(
                    navigateToHome = { navigated = true }
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) { navigated }
        assertTrue(navigated)
    }
}
