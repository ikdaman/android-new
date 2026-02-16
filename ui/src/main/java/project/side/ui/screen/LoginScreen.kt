package project.side.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.auth.LoginUseCase
import project.side.presentation.model.LoginUIState
import project.side.presentation.viewmodel.LoginViewModel
import project.side.ui.R
import project.side.ui.theme.IkdamanTheme

private val MainDescText = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    color = Color.Black,
    letterSpacing = (-0.4).sp
)

private val SubDescText = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = Color.Black,
    letterSpacing = (-0.4).sp
)

private val TermsRegularText = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = Color.Black,
    letterSpacing = (-0.32).sp
)

private val TermsBoldText = TermsRegularText.copy(
    fontWeight = FontWeight.Bold
)

private val LoginButtonText = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 19.sp,
    letterSpacing = (-0.4).sp,
    color = Color.Black
)

private const val TERMS_URL = "https://scientific-ferryboat-eb1.notion.site/19f4710961a980499b90cb88b2c2ec0d?source=copy_link"
private const val PRIVACY_URL = "https://scientific-ferryboat-eb1.notion.site/19f4710961a9807f98a8e1617d31b4bd?source=copy_link"

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
                .background(Color.White)
        ) {
            if (uiState is LoginUIState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(111.dp))
                Image(
                    painter = painterResource(R.drawable.book),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
                Spacer(Modifier.height(36.dp))
                Text(
                    text = "마음가는 대로 읽는 즐거움\n읽다만.",
                    style = MainDescText,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(19.dp))
                Text(
                    text = "읽다만에 로그인하고\n더 즐거운 독서를 시작해보세요 \u263A\uFE0F",
                    style = SubDescText,
                    textAlign = TextAlign.Center
                )
                Box(Modifier.weight(1f))
                Row {
                    Text(text = "가입 시 ", style = TermsRegularText)
                    TermText("이용약관") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL)))
                    }
                    Text(text = " 및 ", style = TermsRegularText)
                    TermText("개인정보처리방침") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
                    }
                    Text(text = "에 동의하게 됩니다.", style = TermsRegularText)
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 15.dp)
                ) {
                    SocialButton(
                        text = "구글로 시작하기",
                        backgroundColor = Color(0xFFFFFFFF),
                        textColor = Color(0xFF1F1F1F),
                        borderColor = Color(0xFF747775),
                        imageResId = R.drawable.google_logo
                    ) {
                        if (loginUseCase != null) viewModel?.googleLogin(loginUseCase)
                    }
                    Spacer(Modifier.height(10.dp))
                    SocialButton(
                        text = "네이버로 시작하기",
                        backgroundColor = Color(0xFF03C75A),
                        textColor = Color(0xFFFFFFFF),
                        imageResId = R.drawable.naver_logo
                    ) {
                        if (loginUseCase != null) viewModel?.naverLogin(loginUseCase)
                    }
                    Spacer(Modifier.height(10.dp))
                    SocialButton(
                        text = "카카오로 시작하기",
                        backgroundColor = Color(0xFFFEE500),
                        textColor = Color(0xD9000000),
                        imageResId = R.drawable.kakao_logo
                    ) {
                        if (loginUseCase != null) viewModel?.kakaoLogin(loginUseCase)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun TermText(text: String, onClick: () -> Unit = {}) {
    Column {
        Text(
            text = text,
            style = TermsBoldText,
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

@Composable
private fun SocialButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color? = null,
    imageResId: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .then(
                if (borderColor == null) Modifier
                else Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
            )
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(text = text, style = LoginButtonText.copy(color = textColor))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LoginScreenPreview() {
    IkdamanTheme {
        LoginScreen(viewModel = null, loginUseCase = null)
    }
}
