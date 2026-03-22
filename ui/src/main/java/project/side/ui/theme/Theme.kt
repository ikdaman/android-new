package project.side.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IkdamanColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextWhite,
    background = BackgroundDefault,
    onBackground = TextPrimary,
    surface = BackgroundWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundGray,
    outline = BorderBlack
)

@Composable
fun IkdamanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IkdamanColorScheme,
        typography = Typography,
        content = content
    )
}
