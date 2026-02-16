package project.side.ui.util

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun Modifier.noEffectClick(
    crossinline onClick: () -> Unit,
): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}

inline fun Modifier.oneClick(crossinline onClick: () -> Unit): Modifier = composed {
    oneClick(500L, onClick)
}

inline fun Modifier.oneClick(delay: Long, crossinline onClick: () -> Unit): Modifier = composed {
    val buttonState = remember { mutableStateOf(true) }
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        if (buttonState.value) {
            buttonState.value = false
            onClick()
            Handler(Looper.getMainLooper()).postDelayed({
                buttonState.value = true
            }, delay)
        }
    }
}

@Composable
fun rememberOneClickHandler(delay: Long = 500L): ((() -> Unit) -> Unit) {
    val enabled = remember { mutableStateOf(true) }
    return { action ->
        if (enabled.value) {
            enabled.value = false
            action()
            Handler(Looper.getMainLooper()).postDelayed({
                enabled.value = true
            }, delay)
        }
    }
}