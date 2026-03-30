package project.side.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import project.side.ui.theme.BackgroundDefault

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        navigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDefault)
    )
}
