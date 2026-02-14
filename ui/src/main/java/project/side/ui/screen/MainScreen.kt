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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import project.side.presentation.viewmodel.BookInfoViewModel
import project.side.presentation.viewmodel.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.presentation.viewmodel.SearchBookViewModel
import project.side.presentation.util.SnackbarManager
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import project.side.ui.component.CustomSnackbarHost
import project.side.ui.ADD_BOOK_ROUTE
import project.side.ui.BOOK_INFO_ROUTE
import project.side.ui.HISTORY_ROUTE
import project.side.ui.HOME_ROUTE
import project.side.ui.MANUAL_BOOK_INPUT_ROUTE
import project.side.ui.SEARCH_BOOK_ROUTE
import project.side.ui.LOGIN_ROUTE
import project.side.ui.MAIN_ROUTE
import project.side.ui.SETTING_ROUTE
import project.side.domain.model.StoreBookItem
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.ui.component.BottomNavBar
import project.side.ui.util.navigateIfLoggedIn

@Composable
fun MainScreen(
    appNavController: NavController,
    searchBookViewModel: SearchBookViewModel? = null,
    logoutUseCase: LogoutUseCase? = null,
    getProviderUseCase: GetProviderUseCase? = null,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn = mainViewModel.isLoggedIn.collectAsState()
    val nickname by mainViewModel.nickname.collectAsState()
    val storeBooks by mainViewModel.storeBooks.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    // 홈 화면에 돌아올 때 내 서점 목록 새로고침
    LaunchedEffect(currentRoute) {
        if (currentRoute == HOME_ROUTE) {
            mainViewModel.refreshStoreBooks()
        }
    }
    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState)
        },
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
            LaunchedEffect(Unit) {
                SnackbarManager.events.collectLatest { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }
            NavHost(
                navController = navController,
                startDestination = HOME_ROUTE
            ) {
                composable(HOME_ROUTE) {
                    HomeScreen(
                        nickname = nickname,
                        storeBooks = storeBooks,
                        onLoadMore = { mainViewModel.loadMore() },
                        onBookClick = { mybookId ->
                            navController.navigate("BookInfo/$mybookId")
                        },
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
                    SearchBookScreen(
                        appNavController,
                        onNavigateToAddBookScreen = {
                            appNavController.navigate(ADD_BOOK_ROUTE)
                        },
                        onNavigateToManualInputScreen = {
                            appNavController.navigate(MANUAL_BOOK_INPUT_ROUTE)
                        },
                        viewModel = searchBookViewModel
                    )
                }
                composable(HISTORY_ROUTE) {
                    HistoryScreen(
                        viewModel = hiltViewModel(),
                        onBookClick = { mybookId ->
                            navController.navigate("BookInfo/$mybookId")
                        }
                    )
                }
                composable(SETTING_ROUTE) {
                    SettingScreen(
                        logoutUseCase = logoutUseCase,
                        getProviderUseCase = getProviderUseCase,
                        onLogoutComplete = {
                            appNavController.navigate(LOGIN_ROUTE) {
                                popUpTo(MAIN_ROUTE) { inclusive = true }
                            }
                        }
                    )
                }
                composable(
                    BOOK_INFO_ROUTE,
                    arguments = listOf(navArgument("mybookId") { type = NavType.IntType })
                ) {
                    val bookInfoViewModel: BookInfoViewModel = hiltViewModel()
                    BookInfoScreen(
                        viewModel = bookInfoViewModel,
                        onBack = { navController.popBackStack() },
                        onDeleteComplete = {
                            navController.popBackStack(HOME_ROUTE, inclusive = false)
                        }
                    )
                }
            }
        }
    }
}
