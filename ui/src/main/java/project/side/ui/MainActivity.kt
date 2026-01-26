package project.side.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import project.side.domain.model.DomainAuthEvent
import project.side.domain.usecase.GetAuthEventUseCase
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.ui.screen.LoginScreen
import project.side.ui.screen.MainScreen
import project.side.ui.theme.IkdamanTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Inject
    lateinit var getAuthEventUseCase: GetAuthEventUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IkdamanTheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    getAuthEventUseCase()
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

                NavHost(navController = navController, startDestination = MAIN_ROUTE) {
                    composable(MAIN_ROUTE) {
                        MainScreen(navController, hiltViewModel())
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
                }
            }
        }
    }
}