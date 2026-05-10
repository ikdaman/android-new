package project.side.widget.glance

import androidx.glance.appwidget.GlanceAppWidget
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * LargeWidget 단위 테스트.
 *
 * provideGlance()는 Android 프레임워크(Context, GlanceId) 의존성이 있어
 * instrumented test 영역이므로, 여기서는 구조적 계약을 검증한다.
 *
 * 검증 항목:
 * - GlanceAppWidget 상속 여부
 * - 인스턴스 생성 가능 여부
 * - LargeDeps EntryPoint 인터페이스가 내부에 선언되어 있는지
 */
class LargeWidgetTest {

    @Test
    fun `LargeWidget은 GlanceAppWidget을 상속한다`() {
        assertTrue(
            "LargeWidget must extend GlanceAppWidget",
            GlanceAppWidget::class.java.isAssignableFrom(LargeWidget::class.java),
        )
    }

    @Test
    fun `LargeWidget 인스턴스가 정상 생성된다`() {
        val widget = LargeWidget()
        assertNotNull(widget)
    }

    @Test
    fun `LargeDeps EntryPoint 인터페이스가 내부 클래스로 선언되어 있다`() {
        val innerClasses = LargeWidget::class.java.declaredClasses
        val hasDeps = innerClasses.any { it.simpleName == "LargeDeps" }
        assertTrue(
            "LargeWidget must declare a nested LargeDeps interface",
            hasDeps,
        )
    }
}
