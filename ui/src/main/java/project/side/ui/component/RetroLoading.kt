package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundWhite
import project.side.ui.theme.DungGeunMoBody
import project.side.ui.theme.TextPrimary

/**
 * Wraps content with a retro loading overlay.
 * The overlay stays visible until [isLoading] is false AND the content has rendered.
 */
@Composable
fun RetroLoadingScreen(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showOverlay by remember { mutableStateOf(true) }
    val dotCount = remember { mutableIntStateOf(0) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            showOverlay = true
        } else {
            delay(150)
            showOverlay = false
        }
    }

    LaunchedEffect(showOverlay) {
        while (showOverlay) {
            delay(400)
            dotCount.intValue = (dotCount.intValue + 1) % 4
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDefault),
                contentAlignment = Alignment.Center
            ) {
                PixelShadowBox(
                    backgroundColor = BackgroundWhite,
                    shadowOffset = 3.dp
                ) {
                    Text(
                        text = "로딩중" + ".".repeat(dotCount.intValue),
                        style = DungGeunMoBody,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Standalone retro loading indicator (for simple cases).
 */
@Composable
fun RetroLoading(modifier: Modifier = Modifier) {
    val dotCount = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            dotCount.intValue = (dotCount.intValue + 1) % 4
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDefault),
        contentAlignment = Alignment.Center
    ) {
        PixelShadowBox(
            backgroundColor = BackgroundWhite,
            shadowOffset = 3.dp
        ) {
            Text(
                text = "로딩중" + ".".repeat(dotCount.intValue),
                style = DungGeunMoBody,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }
    }
}
