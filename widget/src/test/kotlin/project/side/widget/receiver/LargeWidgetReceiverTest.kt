package project.side.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import project.side.widget.glance.LargeWidget

/**
 * LargeWidgetReceiver 단위 테스트.
 *
 * GlanceAppWidgetReceiver 상속 및 LargeWidget 연결 여부를 검증한다.
 * onUpdate()의 WidgetUpdater.refreshAll() 호출은 Android 프레임워크
 * 의존성(AppWidgetManager) 때문에 instrumented test 영역이므로
 * 여기서는 구조적 계약만 확인한다.
 */
class LargeWidgetReceiverTest {

    @Test
    fun `LargeWidgetReceiver는 GlanceAppWidgetReceiver를 상속한다`() {
        assertTrue(
            "LargeWidgetReceiver must extend GlanceAppWidgetReceiver",
            GlanceAppWidgetReceiver::class.java.isAssignableFrom(LargeWidgetReceiver::class.java),
        )
    }

    @Test
    fun `glanceAppWidget 필드는 GlanceAppWidget 타입이어야 한다`() {
        val field = LargeWidgetReceiver::class.java
            .getDeclaredField("glanceAppWidget")
        field.isAccessible = true
        assertTrue(
            "glanceAppWidget field must be of type GlanceAppWidget",
            GlanceAppWidget::class.java.isAssignableFrom(field.type),
        )
    }

    @Test
    fun `LargeWidget 인스턴스가 정상 생성된다`() {
        val widget = LargeWidget()
        assertNotNull(widget)
    }
}
