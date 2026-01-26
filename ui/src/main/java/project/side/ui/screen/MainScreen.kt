package project.side.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import project.side.presentation.viewmodel.MainViewModel
import project.side.ui.ADD_BOOK_ROUTE
import project.side.ui.BOOK_INFO_ROUTE
import project.side.ui.HISTORY_ROUTE
import project.side.ui.HOME_ROUTE
import project.side.ui.SEARCH_BOOK_ROUTE
import project.side.ui.SETTING_ROUTE
import project.side.ui.component.BottomNavBar
import project.side.ui.util.navigateIfLoggedIn

@Composable
fun MainScreen(appNavController: NavController, mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn = mainViewModel.isLoggedIn.collectAsState()

    Scaffold(
        bottomBar = {
            if (currentRoute != SETTING_ROUTE && currentRoute != BOOK_INFO_ROUTE) {
                BottomNavBar(navController) { onLoggedIn ->
                    appNavController.navigateIfLoggedIn(isLoggedIn.value) { onLoggedIn() }
                }
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
                        navigateToSetting = {
                            appNavController.navigateIfLoggedIn(isLoggedIn.value) {
                                navController.navigate(SETTING_ROUTE)
                            }
                        },
                        navigateToSearchBook = {
                            navController.navigate(SEARCH_BOOK_ROUTE)
                        }
                    )
                }
                composable(SEARCH_BOOK_ROUTE) {
                    SearchBookScreen()
                }
                composable(HISTORY_ROUTE) {
                    HistoryScreen(hiltViewModel())
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