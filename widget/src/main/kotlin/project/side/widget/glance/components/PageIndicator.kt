package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

private const val DOT_SIZE_DP = 6
private const val DOT_HALF_SPACING_DP = 2
private const val ARROW_PADDING_DP = 8

@Composable
fun PageIndicator(
    total: Int,
    current: Int,
    activeColor: Color,
    inactiveColor: Color,
    onPrev: Action,
    onNext: Action,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = ARROW_PADDING_DP.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
    ) {
        // 이전 화살표
        Text(
            text = "◀",
            modifier = GlanceModifier
                .clickable(onPrev)
                .padding(ARROW_PADDING_DP.dp),
            style = TextStyle(color = ColorProvider(activeColor)),
        )

        // 점 인디케이터 (Spacer 대신 padding으로 spacing — Glance Row 의 10-element 제한 회피)
        repeat(total) { index ->
            val dotColor = if (index == current) activeColor else inactiveColor
            Box(
                modifier = GlanceModifier
                    .padding(horizontal = DOT_HALF_SPACING_DP.dp)
                    .size(DOT_SIZE_DP.dp)
                    .cornerRadius(DOT_SIZE_DP / 2)
                    .background(ColorProvider(dotColor)),
            ) {}
        }

        // 다음 화살표
        Text(
            text = "▶",
            modifier = GlanceModifier
                .clickable(onNext)
                .padding(ARROW_PADDING_DP.dp),
            style = TextStyle(color = ColorProvider(activeColor)),
        )
    }
}
