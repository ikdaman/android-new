package project.side.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.viewmodel.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    loginUseCase: LoginUseCase? = null,
    logoutUseCase: LogoutUseCase? = null,
    viewModel: LoginViewModel? = hiltViewModel(),
    uiState: LoginViewModel.UIState? = viewModel?.uiState?.collectAsState()?.value,
    navigateToHome: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text("읽고 싶은 책", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(12.dp))
                Text("로그인하세요!", style = MaterialTheme.typography.titleSmall)
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                Text("가입시 이용약관 및 개인정보처리방침에 동의하게 됩니다.", fontSize = 12.sp)
                Button(onClick = {
                    viewModel?.googleLogin(loginUseCase!!)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Google Login")
                }
                Button(onClick = {
                    viewModel?.googleLogout(logoutUseCase!!)
                }) {
                    Text("Google Logout")
                }
                Button(onClick = {
                    viewModel?.naverLogin(loginUseCase!!)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Naver Login")
                }
                Button(onClick = {
                    viewModel?.naverLogout(logoutUseCase!!)
                }) {
                    Text("Naver Logout")
                }
                Button(onClick = {
                    viewModel?.kakaoLogin(loginUseCase!!)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Kakao Login")
                }
                Button(onClick = {
                    viewModel?.kakaoLogout(logoutUseCase!!)
                }) {
                    Text("Kakao Logout")
                }
            }
        }

        LaunchedEffect(uiState) {
            var message = ""
            if (uiState is LoginViewModel.UIState.Error) {
                message = uiState.message
            }
            if (uiState is LoginViewModel.UIState.Success) {
                navigateToHome()
            }
            if (message.isNotEmpty()) {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LoginScreenPreview() = LoginScreen(viewModel = null, loginUseCase = null)