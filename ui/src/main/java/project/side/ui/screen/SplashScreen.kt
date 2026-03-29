package project.side.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.ui.R
import project.side.ui.theme.BackgroundDefault

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
        Image(
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = "읽다만",
            modifier = Modifier.size(120.dp)
        )
    }
}
