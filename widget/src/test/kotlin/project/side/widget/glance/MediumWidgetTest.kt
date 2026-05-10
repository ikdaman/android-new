package project.side.widget.glance

import androidx.glance.appwidget.GlanceAppWidget
import org.junit.Test

/**
 * MediumWidget은 T13에서 본 구현이 들어올 stub입니다.
 * 현재는 GlanceAppWidget을 상속하는지만 검증합니다.
 */
class MediumWidgetTest {

    @Test
    fun `MediumWidget extends GlanceAppWidget`() {
        val widget = MediumWidget()
        assert(widget is GlanceAppWidget) {
            "MediumWidget must extend GlanceAppWidget"
        }
    }

    @Test
    fun `MediumWidget can be instantiated`() {
        val widget = MediumWidget()
        assert(widget != null)
    }
}
