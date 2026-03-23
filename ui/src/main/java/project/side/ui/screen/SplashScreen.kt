package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.first
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.DungGeunMoHomeTitle
import project.side.ui.theme.TextPrimary

@Composable
fun SplashScreen(
    getLoginStateUseCase: GetLoginStateUseCase,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        val isLoggedIn = getLoginStateUseCase().first()
        if (isLoggedIn) {
            navigateToHome()
        } else {
            navigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "읽고 싶은 책",
            style = DungGeunMoHomeTitle,
            color = TextPrimary
        )
    }
}
