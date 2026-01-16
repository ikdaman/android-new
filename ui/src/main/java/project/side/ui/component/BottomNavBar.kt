package project.side.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import project.side.ui.HISTORY_ROUTE
import project.side.ui.HOME_ROUTE
import project.side.ui.R
import project.side.ui.SEARCH_BOOK_ROUTE
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Typography

@Composable
fun BottomNavBar(navController: NavController, navigateIfLoggedIn: (() -> Unit) -> Unit = {}) {
    Column {
        HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 87.dp)
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate(HOME_ROUTE) {
                            popUpTo(0)
                        }
                    },
                resId = R.drawable.ic_home,
                label = "내 서점"
            )

            BottomNavItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate(SEARCH_BOOK_ROUTE) {
                            popUpTo(0)
                        }
                    },
                resId = R.drawable.ic_add_book,
                label = "책 추가"
            )

            BottomNavItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navigateIfLoggedIn {
                            navController.navigate(HISTORY_ROUTE) {
                                popUpTo(0)
                            }
                        }
                    },
                resId = R.drawable.ic_history,
                label = "히스토리"
            )
        }
    }
}

@Composable
fun BottomNavItem(modifier: Modifier, resId: Int, label: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(resId),
            contentDescription = null
        )
        Text(text = label, textAlign = TextAlign.Center, style = Typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    IkdamanTheme {
        val navController = rememberNavController()
        BottomNavBar(navController)
    }
}