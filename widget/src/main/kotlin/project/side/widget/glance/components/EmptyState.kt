package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun EmptyState(
    textColor: Color,
    onClick: Action,
    fontSizeSp: Int = 14,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "읽고 싶은 책을 추가해 보세요 !",
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = fontSizeSp.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
