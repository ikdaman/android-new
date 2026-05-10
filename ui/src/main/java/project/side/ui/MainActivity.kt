package project.side.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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

    private val pendingTarget = mutableStateOf<WidgetTarget?>(null)
    private val pendingSeq = mutableStateOf(0)

    private fun extractWidgetTarget(intent: Intent?): WidgetTarget? {
        if (intent == null) return null
        val target = intent.getStringExtra("widget_target") ?: return null
        return when (target) {
            "book" -> WidgetTarget.Book(intent.getIntExtra("mybook_id", -1))
            "home" -> WidgetTarget.Home
            else -> null
        }
    }

    private fun setPending(target: WidgetTarget?) {
        pendingTarget.value = target
        if (target != null) pendingSeq.value = pendingSeq.value + 1
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractWidgetTarget(intent)?.let { setPending(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPending(extractWidgetTarget(intent))
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

                val target by pendingTarget
                val seq by pendingSeq
                LaunchedEffect(seq) {
                    val t = target ?: return@LaunchedEffect
                    when (t) {
                        is WidgetTarget.Book -> {
                            // BookInfo는 MainScreen 내부 sub-NavHost에 있으므로
                            // MAIN_ROUTE로 먼저 이동시키고 MainScreen이 widgetTarget을 처리.
                            if (t.mybookId != -1) {
                                navController.navigate(MAIN_ROUTE) {
                                    popUpTo(MAIN_ROUTE) { inclusive = true }
                                }
                            } else {
                                pendingTarget.value = null
                            }
                        }
                        WidgetTarget.Home -> {
                            navController.navigate(MAIN_ROUTE) {
                                popUpTo(MAIN_ROUTE) { inclusive = true }
                            }
                            pendingTarget.value = null
                        }
                    }
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
                        MainScreen(
                            appNavController = navController,
                            widgetTarget = pendingTarget.value,
                            widgetSeq = pendingSeq.value,
                            onWidgetTargetConsumed = { pendingTarget.value = null }
                        )
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
