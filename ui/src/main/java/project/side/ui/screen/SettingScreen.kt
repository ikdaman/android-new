package project.side.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.model.SettingUIState
import project.side.presentation.viewmodel.SettingViewModel
import project.side.ui.component.TitleBar
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.TextWhite
import project.side.ui.theme.WantedSansBody

private const val TERMS_URL = "https://www.notion.so/19f4710961a980499b90cb88b2c2ec0d"
private const val PRIVACY_URL = "https://www.notion.so/19f4710961a9807f98a8e1617d31b4bd"

@Composable
fun SettingScreen(
    logoutUseCase: LogoutUseCase? = null,
    getProviderUseCase: GetProviderUseCase? = null,
    viewModel: SettingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onLogoutComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val nickname by viewModel.nickname.collectAsState()
    val isEditing by viewModel.isEditingNickname.collectAsState()
    val nicknameError by viewModel.nicknameError.collectAsState()
    var editText by remember(nickname) { mutableStateOf(nickname) }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is SettingUIState.LogoutSuccess) onLogoutComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault)
    ) {
        TitleBar(
            title = "설 정",
            showBackButton = true,
            onBackButtonClicked = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 30.dp)
        ) {
            // Greeting - DungGeunMo 28px, 2 lines
            Text(
                text = "${nickname.ifEmpty { "OO" }}님,\n안녕하세요!",
                style = DungGeunMoHomeTitle,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Nickname label
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "닉네임",
                    style = DungGeunMoBody,
                    color = TextPrimary
                )
            }

            // Nickname field
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = editText,
                        onValueChange = {
                            editText = it
                            viewModel.validateNickname(it)
                        },
                        textStyle = WantedSansBody.copy(color = TextPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .background(BackgroundWhite)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        singleLine = true
                    )
                    IconButton(onClick = { viewModel.updateNickname(editText) }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "저장", modifier = Modifier.size(20.dp), tint = TextPrimary)
                    }
                    IconButton(onClick = {
                        viewModel.cancelEditingNickname()
                        editText = nickname
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "취소", modifier = Modifier.size(20.dp), tint = TextPrimary)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(BackgroundWhite)
                        .clickable {
                            editText = nickname
                            viewModel.startEditingNickname()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = nickname.ifEmpty { "닉네임 없음" },
                        style = WantedSansBody,
                        color = TextPrimary
                    )
                }
            }

            // Error messages - 2 separate lines
            if (nicknameError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "중복된 닉네임이에요.",
                    color = Primary,
                    style = DungGeunMoTag
                )
                Text(
                    text = "닉네임을 다시 확인해주세요.",
                    color = Primary,
                    style = DungGeunMoTag
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Menu items - DungGeunMo 16px, 32dp touch area each
            SettingMenuItem(text = "공지사항") { /* disabled */ }
            SettingMenuItem(text = "서비스 이용약관") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL)))
            }
            SettingMenuItem(text = "개인정보 처리방침") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout
            Button(
                onClick = { if (logoutUseCase != null && getProviderUseCase != null) viewModel.logout(logoutUseCase, getProviderUseCase) },
                enabled = uiState !is SettingUIState.Loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    if (uiState is SettingUIState.Loading) "로그아웃 중..." else "로그아웃",
                    style = DungGeunMoBody,
                    color = TextWhite
                )
            }

            if (uiState is SettingUIState.Error) {
                Text(
                    text = (uiState as SettingUIState.Error).message,
                    color = Primary,
                    style = DungGeunMoTag,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingMenuItem(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clickable { onClick() }
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = DungGeunMoBody,
            color = TextPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    IkdamanTheme {
        SettingScreen()
    }
}
