package project.side.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import project.side.presentation.model.LoginUIState
import project.side.presentation.viewmodel.LoginViewModel
import project.side.ui.R
import project.side.ui.component.CustomSnackbarHost
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.DungGeunMoSubtitle
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBodySmall

private const val TERMS_URL = "https://scientific-ferryboat-eb1.notion.site/3354710961a98025a529d8e3bb765d2a"
private const val PRIVACY_URL = "https://scientific-ferryboat-eb1.notion.site/3354710961a9809caafdf17937d5dc80"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    viewModel: LoginViewModel? = hiltViewModel(),
    onBackClick: (() -> Unit)? = null,
    infoMessage: String? = null,
    navigateToHome: () -> Unit = {},
    navigateToSignup: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val uiState = viewModel?.uiState?.collectAsState()?.value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(infoMessage) {
        if (!infoMessage.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(infoMessage)
        }
    }

    LaunchedEffect(uiState) {
        var message = ""
        if (uiState is LoginUIState.SignupRequired) {
            navigateToSignup(uiState.socialToken, uiState.provider, uiState.providerId)
            viewModel?.resetState()
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
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDefault)
        ) {
            // Loading dialog
            if (uiState is LoginUIState.Loading) {
                Dialog(onDismissRequest = {}) {
                    val dotCount by remember { mutableIntStateOf(0) }.also { state ->
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(500)
                                state.intValue = (state.intValue % 3) + 1
                            }
                        }
                    }
                    PixelShadowBox(
                        backgroundColor = BackgroundWhite
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "로그인 중" + ".".repeat(dotCount),
                                style = DungGeunMoBody,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (onBackClick != null) {
                    project.side.ui.component.TitleBar(
                        title = "",
                        showBackButton = true,
                        onBackButtonClicked = onBackClick
                    )
                }

                // App logo + title
                Spacer(Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.ic_app_logo),
                    contentDescription = "모아북",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "모아북",
                    style = DungGeunMoHomeTitle,
                    color = TextPrimary
                )
                Spacer(Modifier.weight(1f))

                // Social login buttons
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SocialLoginButton(
                        iconRes = R.drawable.google_logo,
                        text = "구글 로그인",
                        onClick = { viewModel?.googleLogin() }
                    )
                    Spacer(Modifier.height(12.dp))
                    SocialLoginButton(
                        iconRes = R.drawable.naver_logo,
                        text = "네이버 로그인",
                        onClick = { viewModel?.naverLogin() }
                    )
                    Spacer(Modifier.height(12.dp))
                    SocialLoginButton(
                        iconRes = R.drawable.kakao_logo,
                        text = "카카오 로그인",
                        onClick = { viewModel?.kakaoLogin() }
                    )
                }

                // Terms
                Spacer(Modifier.height(16.dp))
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
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SocialLoginButton(
    iconRes: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    PixelShadowButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        backgroundColor = BackgroundWhite
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = DungGeunMoSubtitle,
                color = TextPrimary
            )
        }
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
        LoginScreen(viewModel = null)
    }
}
