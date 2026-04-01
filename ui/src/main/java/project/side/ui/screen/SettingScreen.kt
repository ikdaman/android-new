package project.side.ui.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import project.side.ui.theme.BorderBlack
import project.side.ui.theme.DungGeunMoPopupTitle
import project.side.ui.util.noEffectClick
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.model.SettingUIState
import project.side.presentation.viewmodel.SettingViewModel
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.component.TitleBar
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.DungGeunMoTag
import project.side.ui.theme.IkdamanTheme
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.TextWhite
import project.side.ui.theme.WantedSansBody

private const val TERMS_URL = "https://scientific-ferryboat-eb1.notion.site/3354710961a98025a529d8e3bb765d2a"
private const val PRIVACY_URL = "https://scientific-ferryboat-eb1.notion.site/3354710961a9809caafdf17937d5dc80"

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

    LaunchedEffect(uiState) {
        if (uiState is SettingUIState.LogoutSuccess || uiState is SettingUIState.WithdrawSuccess) onLogoutComplete()
    }

    SettingScreenUI(
        uiState = uiState,
        nickname = nickname,
        isEditing = isEditing,
        nicknameError = nicknameError,
        onBack = onBack,
        onValidateNickname = viewModel::validateNickname,
        onStartEditing = viewModel::startEditingNickname,
        onCancelEditing = viewModel::cancelEditingNickname,
        onSaveNickname = viewModel::updateNickname,
        onLogout = { if (logoutUseCase != null && getProviderUseCase != null) viewModel.logout(logoutUseCase, getProviderUseCase) },
        onWithdraw = viewModel::withdraw
    )
}

@Composable
fun SettingScreenUI(
    uiState: SettingUIState = SettingUIState.Init,
    nickname: String = "",
    isEditing: Boolean = false,
    nicknameError: String? = null,
    onBack: () -> Unit = {},
    onValidateNickname: (String) -> Unit = {},
    onStartEditing: () -> Unit = {},
    onCancelEditing: () -> Unit = {},
    onSaveNickname: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onWithdraw: () -> Unit = {}
) {
    var editText by remember(nickname) { mutableStateOf(nickname) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showWithdrawDialog) {
        Dialog(onDismissRequest = { showWithdrawDialog = false }) {
            PixelShadowBox(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BackgroundWhite,
                shadowOffset = 3.dp,
                contentAlignment = Alignment.TopStart
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .background(BackgroundGray)
                                .border(1.dp, BorderBlack)
                        )
                        Box(
                            modifier = Modifier
                                .width(29.dp)
                                .height(28.dp)
                                .background(BackgroundGray)
                                .border(1.dp, BorderBlack)
                                .noEffectClick { showWithdrawDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("X", style = DungGeunMoBody, color = TextPrimary)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundDefault)
                            .padding(20.dp)
                    ) {
                        Text("회원탈퇴", style = DungGeunMoPopupTitle, color = TextPrimary)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "탈퇴하면 모든 데이터가 삭제되며\n복구할 수 없어요.\n정말로 탈퇴하시겠어요?",
                            style = WantedSansBody,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PixelShadowButton(
                                onClick = { showWithdrawDialog = false },
                                backgroundColor = BackgroundGray
                            ) {
                                Text(
                                    "NO", style = DungGeunMoBody, color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(50.dp))
                            PixelShadowButton(
                                onClick = {
                                    showWithdrawDialog = false
                                    onWithdraw()
                                },
                                backgroundColor = BackgroundGray
                            ) {
                                Text(
                                    "YES", style = DungGeunMoBody, color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
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
                PixelShadowBox(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BackgroundWhite,
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = editText,
                        onValueChange = {
                            editText = it
                            onValidateNickname(it)
                        },
                        textStyle = WantedSansBody.copy(color = TextPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        singleLine = true
                    )
                }

                if (nicknameError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = nicknameError ?: "",
                        color = Primary,
                        style = DungGeunMoTag
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelShadowButton(
                        onClick = {
                            onCancelEditing()
                            editText = nickname
                        },
                        backgroundColor = BackgroundGray
                    ) {
                        Text(
                            "취소",
                            style = DungGeunMoBody,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    PixelShadowButton(
                        onClick = { onSaveNickname(editText) },
                        backgroundColor = Primary
                    ) {
                        Text(
                            "저장",
                            style = DungGeunMoBody,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            } else {
                PixelShadowButton(
                    onClick = {
                        editText = nickname
                        onStartEditing()
                    },
                    backgroundColor = BackgroundWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nickname.ifEmpty { "닉네임 없음" },
                            style = WantedSansBody,
                            color = TextPrimary
                        )
                        Text(
                            text = "수정",
                            style = DungGeunMoTag,
                            color = TextPrimary.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Menu items - DungGeunMo 16px, 32dp touch area each
            SettingMenuItem(text = "공지사항") { /* disabled */ }
            Spacer(Modifier.height(10.dp))
            SettingMenuItem(text = "서비스 이용약관") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL)))
            }
            Spacer(Modifier.height(10.dp))
            SettingMenuItem(text = "개인정보 처리방침") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 회원탈퇴
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWithdrawDialog = true }
                    .padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "회원탈퇴",
                    style = DungGeunMoTag,
                    color = TextPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout
            PixelShadowButton(
                onClick = onLogout,
                backgroundColor = Primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState is SettingUIState.Loading) "로그아웃 중..." else "로그아웃",
                    style = DungGeunMoBody,
                    color = TextWhite,
                    modifier = Modifier.padding(vertical = 10.dp)
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
        SettingScreenUI(
            nickname = "익다만"
        )
    }
}
