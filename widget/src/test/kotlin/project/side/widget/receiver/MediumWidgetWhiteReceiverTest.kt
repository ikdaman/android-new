package project.side.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.junit.Assert.assertTrue
import org.junit.Test
import project.side.widget.glance.MediumWidget

class MediumWidgetWhiteReceiverTest {

    @Test
    fun `MediumWidgetWhiteReceiver는 GlanceAppWidgetReceiver를 상속한다`() {
        assertTrue(
            "MediumWidgetWhiteReceiver must extend GlanceAppWidgetReceiver",
            GlanceAppWidgetReceiver::class.java.isAssignableFrom(MediumWidgetWhiteReceiver::class.java),
        )
    }

    @Test
    fun `glanceAppWidget은 GlanceAppWidget 타입이어야 한다`() {
        val field = MediumWidgetWhiteReceiver::class.java
            .getDeclaredField("glanceAppWidget")
        field.isAccessible = true
        assertTrue(
            "glanceAppWidget field must be of type GlanceAppWidget",
            GlanceAppWidget::class.java.isAssignableFrom(field.type),
        )
    }

    @Test
    fun `MediumWidget은 GlanceAppWidget을 상속한다`() {
        val widget = MediumWidget()
        assertTrue(
            "MediumWidget must extend GlanceAppWidget",
            widget is GlanceAppWidget,
        )
    }
}
