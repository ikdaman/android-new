package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.SignupUseCase
import project.side.presentation.model.SignupUIState
import project.side.presentation.viewmodel.SignupViewModel

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                "회원가입",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "사용할 닉네임을 입력해주세요",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "최대 10자/한글영어숫자가능",
                style = TextStyle(fontSize = 12.sp, color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp)
            )

            if (showDuplicateError) {
                Text(
                    text = "중복된 닉네임이에요.\n닉네임을 다시 확인해주세요.",
                    style = TextStyle(fontSize = 12.sp, color = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.signup(signupUseCase, socialToken, provider, providerId, nickname) },
                enabled = nickname.isNotBlank() && uiState !is SignupUIState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("완료")
            }
        }

        LaunchedEffect(uiState) {
            when (val state = uiState) {
                is SignupUIState.Success -> {
                    onSignupComplete()
                }
                is SignupUIState.Error -> {
                    snackbarHostState.showSnackbar(state.message)
                }
                else -> {}
            }
        }
    }
}
