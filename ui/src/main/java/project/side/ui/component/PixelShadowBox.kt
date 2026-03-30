package project.side.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BorderBlack

@Composable
fun PixelShadowBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundGray,
    shadowColor: Color = BorderBlack,
    shadowOffset: Dp = 1.dp,
    showBorder: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = Modifier.padding(end = shadowOffset, bottom = shadowOffset)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor)
        )
        Box(
            modifier = modifier
                .background(backgroundColor)
                .then(if (showBorder) Modifier.border(1.dp, BorderBlack) else Modifier),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}

@Composable
fun PixelShadowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundGray,
    shadowColor: Color = BorderBlack,
    shadowOffset: Dp = 1.dp,
    isSelected: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val showPressed = isPressed || isSelected

    Box(modifier = Modifier.padding(end = shadowOffset, bottom = shadowOffset)) {
        if (!showPressed) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor)
            )
        }
        Box(
            modifier = modifier
                .background(backgroundColor)
                .then(
                    if (showPressed) {
                        Modifier.drawBehind {
                            val stroke = 1.dp.toPx()
                            val w = size.width
                            val h = size.height
                            // top
                            drawLine(Color.Black, Offset(0f, stroke / 2), Offset(w, stroke / 2), strokeWidth = stroke)
                            // left
                            drawLine(Color.Black, Offset(stroke / 2, 0f), Offset(stroke / 2, h), strokeWidth = stroke)
                            // bottom
                            drawLine(Color.White, Offset(0f, h - stroke / 2), Offset(w, h - stroke / 2), strokeWidth = stroke)
                            // right
                            drawLine(Color.White, Offset(w - stroke / 2, 0f), Offset(w - stroke / 2, h), strokeWidth = stroke)
                        }
                    } else {
                        Modifier.border(1.dp, BorderBlack)
                    }
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}
