package project.side.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.auth.LoginUseCase
import project.side.presentation.model.LoginUIState
import project.side.presentation.viewmodel.LoginViewModel
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody
import project.side.ui.theme.WantedSansBodySmall

private const val TERMS_URL = "https://www.notion.so/19f4710961a980499b90cb88b2c2ec0d"
private const val PRIVACY_URL = "https://www.notion.so/19f4710961a9807f98a8e1617d31b4bd"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    loginUseCase: LoginUseCase? = null,
    viewModel: LoginViewModel? = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    navigateToSignup: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val uiState = viewModel?.uiState?.collectAsState()?.value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        var message = ""
        if (uiState is LoginUIState.SignupRequired) {
            navigateToSignup(uiState.socialToken, uiState.provider, uiState.providerId)
        }
        if (uiState is LoginUIState.Error) {
            message = uiState.message
        }
        if (uiState is LoginUIState.Success) {
            navigateToHome()
        }
        if (message.isNotEmpty()) {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDefault)
        ) {
            if (uiState is LoginUIState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App intro
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .padding(top = 60.dp)
                        .clip(RoundedCornerShape(0.dp))
                        .background(BackgroundWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "읽고 싶은 책\n앱 소개",
                        style = DungGeunMoHomeTitle,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                // Terms
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "가입시 ", style = WantedSansBodySmall, color = TextPrimary)
                    TermText("이용약관") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL)))
                    }
                    Text(text = " 및 ", style = WantedSansBodySmall, color = TextPrimary)
                    TermText("개인정보처리방침") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
                    }
                    Text(text = "에 동의하게 됩니다.", style = WantedSansBodySmall, color = TextPrimary)
                }

                // Social login buttons
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 10.dp, bottom = 40.dp)
                ) {
                    LoginButton("구글 로그인") {
                        if (loginUseCase != null) viewModel?.googleLogin(loginUseCase)
                    }
                    Spacer(Modifier.height(9.dp))
                    LoginButton("네이버 로그인") {
                        if (loginUseCase != null) viewModel?.naverLogin(loginUseCase)
                    }
                    Spacer(Modifier.height(9.dp))
                    LoginButton("카카오 로그인") {
                        if (loginUseCase != null) viewModel?.kakaoLogin(loginUseCase)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginButton(text: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFD9D9D9))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = WantedSansBody,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TermText(text: String, onClick: () -> Unit = {}) {
    Column {
        Text(
            text = text,
            style = WantedSansBodySmall.copy(color = TextPrimary),
            modifier = Modifier
                .drawBehind {
                    val y = size.height + 1.dp.toPx()
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .clickable { onClick() }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LoginScreenPreview() {
    IkdamanTheme {
        LoginScreen(viewModel = null, loginUseCase = null)
    }
}
