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
        // 디자인에 화살표가 빠져서 점만 표시. 페이지 이동은 점 자체 탭으로 가능 — 좌측 절반 탭=이전, 우측 절반 탭=다음.
        repeat(total) { index ->
            val dotColor = if (index == current) activeColor else inactiveColor
            val dotAction = if (index < current) onPrev else onNext
            Box(
                modifier = GlanceModifier
                    .padding(horizontal = DOT_HALF_SPACING_DP.dp)
                    .size(DOT_SIZE_DP.dp)
                    .cornerRadius((DOT_SIZE_DP / 2).dp)
                    .background(ColorProvider(dotColor))
                    .clickable(dotAction),
            ) {}
        }
    }
}
