package project.side.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import project.side.ui.ADD_BOOK_ROUTE
import project.side.ui.BOOK_INFO_ROUTE
import project.side.ui.HISTORY_ROUTE
import project.side.ui.HOME_ROUTE
import project.side.ui.LOGIN_ROUTE
import project.side.ui.SEARCH_BOOK_ROUTE
import project.side.ui.SETTING_ROUTE
import project.side.ui.component.BottomNavBar

@Composable
fun MainScreen(appNavController: NavController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != SETTING_ROUTE && currentRoute != BOOK_INFO_ROUTE) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = HOME_ROUTE
            ) {
                composable(HOME_ROUTE) {
                    HomeScreen(
                        navigateToLogin = navigateToLogin(appNavController),
                        navigateToSetting = { navController.navigate(SETTING_ROUTE) },
                        navigateToSearchBook = { navController.navigate(SEARCH_BOOK_ROUTE) }
                    )
                }
                composable(SEARCH_BOOK_ROUTE) {
                    SearchBookScreen()
                }
                composable(HISTORY_ROUTE) {
                    HistoryScreen()
                }
                composable(SETTING_ROUTE) {
                    SettingScreen()
                }
                composable(ADD_BOOK_ROUTE) {
                    AddBookScreen()
                }
                composable(BOOK_INFO_ROUTE) {
                    BookInfoScreen()
                }
            }
        }
    }
}

fun navigateToLogin(appNavController: NavController): () -> Unit = {
    appNavController.navigate(LOGIN_ROUTE) {
        popUpTo(0)
    }
}