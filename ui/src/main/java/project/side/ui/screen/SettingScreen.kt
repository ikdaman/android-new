package project.side.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import project.side.presentation.model.SettingUIState
import project.side.presentation.viewmodel.SettingViewModel
import project.side.ui.theme.IkdamanTheme

@Composable
fun SettingScreen(
    logoutUseCase: LogoutUseCase? = null,
    getProviderUseCase: GetProviderUseCase? = null,
    viewModel: SettingViewModel? = viewModel(),
    onLogoutComplete: () -> Unit = {}
) {
    val uiState = viewModel?.uiState?.collectAsState()?.value

    LaunchedEffect(uiState) {
        if (uiState is SettingUIState.LogoutSuccess) {
            onLogoutComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("설정", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = { viewModel?.logout(logoutUseCase!!, getProviderUseCase!!) },
            enabled = uiState !is SettingUIState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(if (uiState is SettingUIState.Loading) "로그아웃 중..." else "로그아웃", style = MaterialTheme.typography.labelLarge)
        }

        if (uiState is SettingUIState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    IkdamanTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("설정", style = MaterialTheme.typography.titleLarge)
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("로그아웃")
            }
        }
    }
}
