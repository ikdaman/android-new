package project.side.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import project.side.domain.model.DomainAuthEvent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import project.side.presentation.viewmodel.AuthViewModel
import project.side.presentation.viewmodel.SearchBookViewModel
import project.side.ui.auth.SignupDataHolder
import project.side.ui.screen.AddBookScreen
import project.side.ui.screen.BarcodeScreen
import project.side.ui.screen.LoginScreen
import project.side.ui.screen.MainScreen
import project.side.ui.screen.ManualBookInputScreen
import project.side.ui.screen.SignupScreen
import project.side.ui.screen.SplashScreen
import project.side.ui.theme.IkdamanTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var signupDataHolder: SignupDataHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IkdamanTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = hiltViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                LaunchedEffect(Unit) {
                    authViewModel.authEvent
                        .onEach {
                            when (it) {
                                DomainAuthEvent.LOGIN_REQUIRED -> {
                                    navController.navigate(LOGIN_ROUTE) {
                                        popUpTo(MAIN_ROUTE) { inclusive = true }
                                    }
                                }
                            }
                        }
                        .launchIn(this)
                }

                NavHost(
                    navController = navController,
                    startDestination = SPLASH_ROUTE,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
                ) {
                    composable(SPLASH_ROUTE) {
                        SplashScreen(
                            navigateToHome = {
                                navController.navigate(MAIN_ROUTE) {
                                    popUpTo(SPLASH_ROUTE) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(MAIN_ROUTE) {
                        MainScreen(navController)
                    }
                    composable(BARCODE_ROUTE) {
                        val searchBookViewModel: SearchBookViewModel = hiltViewModel(
                            remember(it) { navController.getBackStackEntry(MAIN_ROUTE) }
                        )
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
                        val canGoBack = remember { navController.previousBackStackEntry != null }
                        LoginScreen(
                            onBackClick = if (canGoBack) { { navController.popBackStack() } } else null,
                            infoMessage = if (canGoBack) "로그인 후 사용이 가능합니다" else null,
                            navigateToHome = {
                                if (canGoBack) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(MAIN_ROUTE) {
                                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                                    }
                                }
                            },
                            navigateToSignup = { socialToken, provider, providerId ->
                                signupDataHolder.set(socialToken, provider, providerId)
                                navController.navigate("Signup")
                            }
                        )
                    }
                    composable(SIGNUP_ROUTE) {
                        val signupData = remember { signupDataHolder.consume() }
                        SignupScreen(
                            socialToken = signupData?.socialToken ?: "",
                            provider = signupData?.provider ?: "",
                            providerId = signupData?.providerId ?: "",
                            onBackClick = { navController.popBackStack() },
                            onSignupComplete = {
                                navController.navigate(MAIN_ROUTE) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(ADD_BOOK_ROUTE) {
                        val searchBookViewModel: SearchBookViewModel = hiltViewModel(
                            remember(it) { navController.getBackStackEntry(MAIN_ROUTE) }
                        )
                        AddBookScreen(
                            appNavController = navController,
                            viewModel = searchBookViewModel,
                            isLoggedIn = isLoggedIn,
                            onLoginRequired = { navController.navigate(LOGIN_ROUTE) }
                        )
                    }
                    composable(MANUAL_BOOK_INPUT_ROUTE) {
                        ManualBookInputScreen(
                            appNavController = navController,
                            isLoggedIn = isLoggedIn,
                            onLoginRequired = { navController.navigate(LOGIN_ROUTE) }
                        )
                    }
                }

            }
        }
    }
}
