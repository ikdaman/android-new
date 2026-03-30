package project.side.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import project.side.domain.model.BookItem
import project.side.ui.theme.IkdamanTheme

class AddBookScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addBookScreen_displaysBookInfo() {
        val testBook = BookItem(
            title = "테스트 책 제목",
            author = "테스트 작가",
            publisher = "테스트 출판사",
            isbn = "1234567890",
            description = "테스트 설명",
            pubDate = "2026-01-01"
        )

        composeTestRule.setContent {
            IkdamanTheme {
                AddBookScreen(
                    appNavController = rememberNavController(),
                    viewModel = null,
                    selectedBook = testBook
                )
            }
        }

        composeTestRule.onNodeWithText("테스트 책 제목").assertIsDisplayed()
        composeTestRule.onNodeWithText("테스트 작가").assertIsDisplayed()
        composeTestRule.onNodeWithText("테스트 출판사").assertIsDisplayed()
    }

    @Test
    fun addBookScreen_saveButton_callsLoginRequired_whenNotLoggedIn() {
        var loginRequested = false

        composeTestRule.setContent {
            IkdamanTheme {
                AddBookScreen(
                    appNavController = rememberNavController(),
                    viewModel = null,
                    selectedBook = BookItem(title = "책"),
                    isLoggedIn = false,
                    onLoginRequired = { loginRequested = true }
                )
            }
        }

        composeTestRule.onNodeWithText("저장").performClick()
        assertTrue("비로그인 시 저장 클릭하면 onLoginRequired 호출", loginRequested)
    }

    @Test
    fun addBookScreen_saveButton_showsBottomSheet_whenLoggedIn() {
        composeTestRule.setContent {
            IkdamanTheme {
                AddBookScreen(
                    appNavController = rememberNavController(),
                    viewModel = null,
                    selectedBook = BookItem(title = "책"),
                    isLoggedIn = true
                )
            }
        }

        composeTestRule.onNodeWithText("저장").performClick()

        // BookRegisterBottomSheet should be visible
        composeTestRule.onNodeWithText("읽고 싶었던 이유").assertIsDisplayed()
    }
}
