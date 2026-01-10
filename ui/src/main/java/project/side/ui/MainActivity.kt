package project.side.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.viewmodel.TestViewModel
import project.side.ui.screen.LoginScreen
import project.side.ui.theme.IkdamanTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel : TestViewModel by viewModels()

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IkdamanTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
//                    TestScreen(viewModel)
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    LoginScreen(loginUseCase = loginUseCase, logoutUseCase = logoutUseCase)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IkdamanTheme {
        Greeting("Android")
    }
}