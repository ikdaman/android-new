package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import project.side.ui.HISTORY_ROUTE
import project.side.ui.HOME_ROUTE
import project.side.ui.SEARCH_BOOK_ROUTE
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.TextWhite

@Composable
fun BottomNavBar(navController: NavController, navigateIfLoggedIn: (() -> Unit) -> Unit = {}) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Row(
        modifier = Modifier
            .background(BackgroundDefault)
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(70.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "내 서점",
            isSelected = currentRoute == HOME_ROUTE,
            onClick = {
                navController.navigate(HOME_ROUTE) { popUpTo(0) }
            }
        )

        VerticalDivider()

        BottomNavItem(
            label = "책 추가",
            isSelected = currentRoute == SEARCH_BOOK_ROUTE,
            onClick = {
                navController.navigate(SEARCH_BOOK_ROUTE) { popUpTo(0) }
            }
        )

        VerticalDivider()

        BottomNavItem(
            label = "히스토리",
            isSelected = currentRoute == HISTORY_ROUTE,
            onClick = {
                navigateIfLoggedIn {
                    navController.navigate(HISTORY_ROUTE) { popUpTo(0) }
                }
            }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(if (isSelected) Primary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = DungGeunMoSubtitle,
            color = if (isSelected) TextWhite else TextPrimary
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(1.dp)
            .height(8.dp)
            .background(TextPrimary)
    )
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    IkdamanTheme {
        val navController = rememberNavController()
        BottomNavBar(navController)
    }
}
