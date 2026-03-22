package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.SignupUseCase
import project.side.presentation.model.SignupUIState
import project.side.presentation.viewmodel.SignupViewModel
import project.side.ui.component.TitleBar
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoHeader
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.WantedSansBody

@Composable
fun SignupScreen(
    socialToken: String,
    provider: String,
    providerId: String,
    signupUseCase: SignupUseCase,
    viewModel: SignupViewModel = hiltViewModel(),
    onSignupComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var nickname by remember { mutableStateOf("") }
    val showDuplicateError = uiState is SignupUIState.NicknameDuplicate

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDefault)
        ) {
            TitleBar(
                title = "책 추가하기",
                showBackButton = true,
                onBackButtonClicked = {},
                rightText = "완료",
                onRightClick = {
                    viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname)
                }
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Title
            Text(
                text = "닉네임을 입력해주세요.",
                style = DungGeunMoHeader,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Nickname input
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                TextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    singleLine = true,
                    textStyle = WantedSansBody.copy(color = TextPrimary),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundWhite,
                        unfocusedContainerColor = BackgroundWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                )

                if (showDuplicateError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "중복된 닉네임이에요.",
                        style = DungGeunMoTag,
                        color = Primary
                    )
                    Text(
                        text = "닉네임을 다시 확인해주세요.",
                        style = DungGeunMoTag,
                        color = Primary
                    )
                }
            }
        }

        LaunchedEffect(uiState) {
            when (val state = uiState) {
                is SignupUIState.Success -> onSignupComplete()
                is SignupUIState.Error -> snackbarHostState.showSnackbar(state.message)
                else -> {}
            }
        }
    }
}
