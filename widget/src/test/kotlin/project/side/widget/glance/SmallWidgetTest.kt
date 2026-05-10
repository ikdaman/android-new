package project.side.widget.glance

import androidx.glance.appwidget.GlanceAppWidget
import org.junit.Test

/**
 * SmallWidget은 T12에서 본 구현이 들어올 stub입니다.
 * 현재는 GlanceAppWidget을 상속하는지만 검증합니다.
 */
class SmallWidgetTest {

    @Test
    fun `SmallWidget extends GlanceAppWidget`() {
        val widget = SmallWidget()
        assert(widget is GlanceAppWidget) {
            "SmallWidget must extend GlanceAppWidget"
        }
    }

    @Test
    fun `SmallWidget can be instantiated`() {
        // stub 클래스가 정상적으로 생성 가능한지 확인
        val widget = SmallWidget()
        assert(widget != null)
    }
}
