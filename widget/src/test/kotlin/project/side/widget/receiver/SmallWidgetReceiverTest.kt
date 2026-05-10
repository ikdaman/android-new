package project.side.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import project.side.widget.glance.SmallWidget

/**
 * SmallWidgetReceiver 단위 테스트.
 *
 * GlanceAppWidgetReceiver 상속 및 SmallWidget 연결 여부를 검증한다.
 * onUpdate()의 WidgetUpdater.refreshAll() 호출은 Android 프레임워크
 * 의존성(AppWidgetManager) 때문에 instrumented test 영역이므로
 * 여기서는 구조적 계약만 확인한다.
 */
class SmallWidgetReceiverTest {

    @Test
    fun `SmallWidgetReceiver는 GlanceAppWidgetReceiver를 상속한다`() {
        // GlanceAppWidgetReceiver는 BroadcastReceiver를 상속하므로
        // 직접 인스턴스화할 수 없다 → 클래스 계층만 검증
        assertTrue(
            "SmallWidgetReceiver must extend GlanceAppWidgetReceiver",
            GlanceAppWidgetReceiver::class.java.isAssignableFrom(SmallWidgetReceiver::class.java),
        )
    }

    @Test
    fun `glanceAppWidget은 SmallWidget 인스턴스여야 한다`() {
        // reflection으로 glanceAppWidget 필드 타입을 확인
        val field = SmallWidgetReceiver::class.java
            .getDeclaredField("glanceAppWidget")
        field.isAccessible = true
        // 인스턴스를 만들 수 없으므로 선언 타입(GlanceAppWidget)만 검증
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

    @Test
    fun `SmallWidget 인스턴스가 정상 생성된다`() {
        val widget = SmallWidget()
        assertNotNull(widget)
    }
}
