package project.side.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.model.SettingUIState
import project.side.presentation.viewmodel.SettingViewModel
import project.side.ui.R
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Typography

private const val TERMS_URL = "https://ikdaman.notion.site/1cd0fa30e02480a98b4bf9e6e1bdeb37?pvs=4"
private const val PRIVACY_URL = "https://ikdaman.notion.site/1cd0fa30e0248028b3e4f28f8419f168?pvs=4"

@Composable
fun SettingScreen(
    logoutUseCase: LogoutUseCase? = null,
    getProviderUseCase: GetProviderUseCase? = null,
    viewModel: SettingViewModel = hiltViewModel(),
    onLogoutComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val nickname by viewModel.nickname.collectAsState()
    val isEditing by viewModel.isEditingNickname.collectAsState()
    val nicknameError by viewModel.nicknameError.collectAsState()
    var editText by remember(nickname) { mutableStateOf(nickname) }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is SettingUIState.LogoutSuccess) {
            onLogoutComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 인사말
        Text(
            text = "${nickname.ifEmpty { "OO" }}님, 안녕하세요 :)",
            style = Typography.bodyLarge.copy(fontSize = 22.sp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 내 정보 관리 섹션
        Text(
            text = "내 정보 관리",
            style = Typography.titleSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 닉네임 편집
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                BasicTextField(
                    value = editText,
                    onValueChange = {
                        editText = it
                        viewModel.validateNickname(it)
                    },
                    textStyle = Typography.bodyMedium.copy(color = Color.Black),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    singleLine = true
                )
                IconButton(onClick = { viewModel.updateNickname(editText) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "저장",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = {
                    viewModel.cancelEditingNickname()
                    editText = nickname
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "취소",
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Text(
                    text = nickname.ifEmpty { "닉네임 없음" },
                    style = Typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    editText = nickname
                    viewModel.startEditingNickname()
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "닉네임 수정",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (nicknameError != null) {
            Text(
                text = nicknameError!!,
                color = MaterialTheme.colorScheme.error,
                style = Typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(24.dp))

        // 메뉴 목록
        // 공지사항 (비활성화)
        Text(
            text = "공지사항",
            style = Typography.bodyMedium,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 이용약관
        Text(
            text = "이용약관",
            style = Typography.bodyMedium,
            modifier = Modifier.clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL)))
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 개인정보처리방침
        Text(
            text = "개인정보처리방침",
            style = Typography.bodyMedium,
            modifier = Modifier.clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // 로그아웃
        Button(
            onClick = { if (logoutUseCase != null && getProviderUseCase != null) viewModel.logout(logoutUseCase, getProviderUseCase) },
            enabled = uiState !is SettingUIState.Loading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                if (uiState is SettingUIState.Loading) "로그아웃 중..." else "로그아웃",
                style = Typography.labelLarge
            )
        }

        if (uiState is SettingUIState.Error) {
            Text(
                text = (uiState as SettingUIState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    IkdamanTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "홍길동님, 안녕하세요 :)",
                style = Typography.bodyLarge.copy(fontSize = 22.sp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text("내 정보 관리", style = Typography.titleSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("홍길동", style = Typography.bodyMedium, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            Text("공지사항", style = Typography.bodyMedium, color = Color.LightGray)
            Spacer(modifier = Modifier.height(20.dp))
            Text("이용약관", style = Typography.bodyMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Text("개인정보처리방침", style = Typography.bodyMedium)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("로그아웃")
            }
        }
    }
}
