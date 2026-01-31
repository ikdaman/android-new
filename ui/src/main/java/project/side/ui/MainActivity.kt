package project.side.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.viewmodel.SearchBookViewModel
import project.side.ui.screen.AddBookScreen
import project.side.ui.screen.BarcodeScreen
import project.side.ui.screen.LoginScreen
import project.side.ui.screen.MainScreen
import project.side.ui.screen.ManualBookInputScreen
import project.side.ui.theme.IkdamanTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IkdamanTheme {
                val navController = rememberNavController()

                val searchBookViewModel: SearchBookViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = MAIN_ROUTE) {
                    composable(MAIN_ROUTE) {
                        MainScreen(navController, searchBookViewModel)
                    }
                    composable(BARCODE_ROUTE) {
                        BarcodeScreen(
                            viewModel = searchBookViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToAddBookScreen = {
                                navController.popBackStack()
                                navController.navigate(ADD_BOOK_ROUTE)
                            }
                        )
                    }
                    composable(LOGIN_ROUTE) {
                        LoginScreen(loginUseCase, logoutUseCase) {
                            navController.navigate(MAIN_ROUTE) {
                                popUpTo(LOGIN_ROUTE) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(ADD_BOOK_ROUTE) {
                        AddBookScreen(
                            appNavController = navController,
                            viewModel = searchBookViewModel
                        )
                    }
                    composable(MANUAL_BOOK_INPUT_ROUTE) {
                        ManualBookInputScreen(
                            appNavController = navController
                        )
                    }
                }
            }
        }
    }
}
