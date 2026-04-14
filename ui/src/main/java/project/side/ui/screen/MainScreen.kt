package project.side.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import project.side.ui.SEARCH_MY_BOOK_ROUTE
import project.side.ui.LOGIN_ROUTE
import project.side.ui.MAIN_ROUTE
import project.side.ui.SETTING_ROUTE
import androidx.compose.runtime.mutableIntStateOf
import project.side.domain.model.StoreBookItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import project.side.ui.component.BottomNavBar
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.component.ReadingStartBottomSheet
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.util.noEffectClick
import project.side.ui.util.navigateIfLoggedIn

@Composable
fun MainScreen(
    appNavController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn = mainViewModel.isLoggedIn.collectAsState()
    val nickname by mainViewModel.nickname.collectAsState()
    val storeBooks by mainViewModel.storeBooks.collectAsState()
    val storeBooksError by mainViewModel.storeBooksError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val showReadingStartSheet = remember { mutableStateOf(false) }
    val readingStartMybookId = remember { mutableIntStateOf(-1) }
    val readingStartBookTitle = remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val deleteMybookId = remember { mutableIntStateOf(-1) }

    if (showDeleteDialog.value) {
        Dialog(onDismissRequest = { showDeleteDialog.value = false }) {
            PixelShadowBox(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BackgroundWhite,
                shadowOffset = 3.dp,
                contentAlignment = Alignment.TopStart
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .background(BackgroundGray)
                                .border(1.dp, BorderBlack)
                        )
                        Box(
                            modifier = Modifier
                                .width(29.dp)
                                .height(28.dp)
                                .background(BackgroundGray)
                                .border(1.dp, BorderBlack)
                                .noEffectClick { showDeleteDialog.value = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("X", style = DungGeunMoBody, color = TextPrimary)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundDefault)
                            .padding(20.dp)
                    ) {
                        Text("책 삭제", style = DungGeunMoPopupTitle, color = TextPrimary)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "책을 삭제하면 모든 기록이 사라져요.\n정말로 삭제하시겠어요?",
                            style = WantedSansBody,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PixelShadowButton(
                                onClick = { showDeleteDialog.value = false },
                                backgroundColor = BackgroundGray
                            ) {
                                Text(
                                    "NO", style = DungGeunMoBody, color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(50.dp))
                            PixelShadowButton(
                                onClick = {
                                    showDeleteDialog.value = false
                                    mainViewModel.deleteBook(deleteMybookId.intValue)
                                },
                                backgroundColor = BackgroundGray
                            ) {
                                Text(
                                    "YES", style = DungGeunMoBody, color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    ReadingStartBottomSheet(
        show = showReadingStartSheet.value,
        bookTitle = readingStartBookTitle.value,
        onDismiss = { showReadingStartSheet.value = false },
        onConfirm = {
            showReadingStartSheet.value = false
            mainViewModel.startReading(readingStartMybookId.intValue)
        }
    )

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
                    mainViewModel.refreshStoreBooks()
                    snackbarHostState.showSnackbar(msg)
                }
            }
            NavHost(
                navController = navController,
                startDestination = HOME_ROUTE,
                enterTransition = { androidx.compose.animation.EnterTransition.None },
                exitTransition = { androidx.compose.animation.ExitTransition.None },
                popEnterTransition = { androidx.compose.animation.EnterTransition.None },
                popExitTransition = { androidx.compose.animation.ExitTransition.None }
            ) {
                composable(HOME_ROUTE) { backStackEntry ->
                    LaunchedEffect(backStackEntry) {
                        mainViewModel.refreshStoreBooks()
                    }
                    val sortDescending by mainViewModel.sortDescending.collectAsState()
                    HomeScreen(
                        nickname = nickname,
                        storeBooks = storeBooks,
                        errorMessage = storeBooksError,
                        sortDescending = sortDescending,
                        onToggleSort = { mainViewModel.toggleSort() },
                        onLoadMore = { mainViewModel.loadMore() },
                        onRetry = { mainViewModel.refreshStoreBooks() },
                        onBookClick = { mybookId ->
                            val desc = storeBooks.find { it.mybookId == mybookId }?.description
                            val encodedDesc = desc?.let { android.net.Uri.encode(it) } ?: ""
                            navController.navigate("BookInfo/$mybookId?description=$encodedDesc")
                        },
                        onStartReading = { mybookId, title ->
                            readingStartMybookId.intValue = mybookId
                            readingStartBookTitle.value = title
                            showReadingStartSheet.value = true
                        },
                        navigateToSetting = {
                            appNavController.navigateIfLoggedIn(isLoggedIn.value) {
                                navController.navigate(SETTING_ROUTE)
                            }
                        },
                        navigateToSearchBook = {
                            navController.navigate(SEARCH_BOOK_ROUTE)
                        },
                        navigateToMyBookSearch = {
                            navController.navigate(SEARCH_MY_BOOK_ROUTE)
                        },
                        onDelete = { mybookId ->
                            deleteMybookId.intValue = mybookId
                            showDeleteDialog.value = true
                        }
                    )
                }
                composable(SEARCH_BOOK_ROUTE) {
                    val searchBookViewModel: SearchBookViewModel = hiltViewModel(
                        remember(it) { appNavController.getBackStackEntry(MAIN_ROUTE) }
                    )
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
                composable(SEARCH_MY_BOOK_ROUTE) {
                    MyBookSearchScreen(
                        onBack = { navController.popBackStack() },
                        onBookClick = { mybookId ->
                            navController.navigate("BookInfo/$mybookId")
                        }
                    )
                }
                composable(HISTORY_ROUTE) { backStackEntry ->
                    val historyViewModel: project.side.presentation.viewmodel.HistoryViewModel = hiltViewModel()
                    LaunchedEffect(backStackEntry) {
                        historyViewModel.getBooks(showLoading = false)
                    }
                    HistoryScreen(
                        viewModel = historyViewModel,
                        onBookClick = { mybookId ->
                            navController.navigate("BookInfo/$mybookId")
                        },
                        onSearchClick = {
                            navController.navigate(SEARCH_MY_BOOK_ROUTE)
                        }
                    )
                }
                composable(SETTING_ROUTE) {
                    SettingScreen(
                        onBack = { navController.popBackStack() },
                        onLogoutComplete = {
                            appNavController.navigate(LOGIN_ROUTE) {
                                popUpTo(MAIN_ROUTE) { inclusive = true }
                            }
                        }
                    )
                }
                composable(
                    BOOK_INFO_ROUTE,
                    arguments = listOf(
                        navArgument("mybookId") { type = NavType.IntType },
                        navArgument("description") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) {
                    val bookInfoViewModel: BookInfoViewModel = hiltViewModel()
                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                    BookInfoScreen(
                        viewModel = bookInfoViewModel,
                        onBack = { navController.popBackStack() },
                        onDeleteComplete = {
                            if (previousRoute == HISTORY_ROUTE) {
                                navController.popBackStack(HISTORY_ROUTE, inclusive = false)
                            } else {
                                navController.popBackStack(HOME_ROUTE, inclusive = false)
                            }
                        }
                    )
                }
            }
        }
    }
}
