package project.side.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.junit.Assert.assertTrue
import org.junit.Test
import project.side.widget.glance.SmallWidget

class SmallWidgetBlueReceiverTest {

    @Test
    fun `SmallWidgetBlueReceiver는 GlanceAppWidgetReceiver를 상속한다`() {
        assertTrue(
            "SmallWidgetBlueReceiver must extend GlanceAppWidgetReceiver",
            GlanceAppWidgetReceiver::class.java.isAssignableFrom(SmallWidgetBlueReceiver::class.java),
        )
    }

    @Test
    fun `glanceAppWidget은 GlanceAppWidget 타입이어야 한다`() {
        val field = SmallWidgetBlueReceiver::class.java
            .getDeclaredField("glanceAppWidget")
        field.isAccessible = true
        assertTrue(
            "glanceAppWidget field must be of type GlanceAppWidget",
            GlanceAppWidget::class.java.isAssignableFrom(field.type),
        )
    }

    @Test
    fun `SmallWidget은 GlanceAppWidget을 상속한다`() {
        val widget = SmallWidget()
        assertTrue(
            "SmallWidget must extend GlanceAppWidget",
            widget is GlanceAppWidget,
        )
    }
}
