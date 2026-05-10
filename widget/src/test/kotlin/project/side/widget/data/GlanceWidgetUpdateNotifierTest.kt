package project.side.widget.data

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * GlanceWidgetUpdateNotifier 단위 테스트.
 *
 * 현재는 stub 구현이므로:
 * - 인스턴스 생성 가능 여부
 * - notifyAllWidgets() 호출 시 예외 없이 완료되는지
 * 를 검증한다.
 *
 * Task 12/13/14에서 실제 GlanceAppWidget.updateAll() 호출이 추가되면
 * 이 테스트도 함께 확장한다.
 */
class GlanceWidgetUpdateNotifierTest {

    @Test
    fun `생성자에 Context를 주입하면 인스턴스가 null이 아니다`() {
        val context = mockk<android.content.Context>(relaxed = true)
        val notifier = GlanceWidgetUpdateNotifier(context)
        assertNotNull(notifier)
    }

    @Test
    fun `notifyAllWidgets는 stub 단계에서 예외 없이 완료된다`() = runTest {
        val context = mockk<android.content.Context>(relaxed = true)
        val notifier = GlanceWidgetUpdateNotifier(context)
        // 예외가 발생하면 테스트 실패
        notifier.notifyAllWidgets()
    }

    @Test
    fun `WidgetUpdateNotifier 인터페이스를 구현한다`() {
        val context = mockk<android.content.Context>(relaxed = true)
        val notifier: WidgetUpdateNotifier = GlanceWidgetUpdateNotifier(context)
        assertNotNull(notifier)
    }
}
