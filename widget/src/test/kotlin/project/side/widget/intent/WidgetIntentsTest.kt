package project.side.widget.intent

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WidgetIntents의 Android Intent 생성 로직은 Robolectric 없이는 JVM 단위 테스트로 검증이 어렵습니다.
 * 여기서는 Intent에 담기는 상수값이 올바른지만 검증합니다.
 */
class WidgetIntentsTest {

    @Test
    fun `MAIN_ACTIVITY_CLASS points to correct class`() {
        assertEquals("project.side.ui.MainActivity", WidgetIntents.MAIN_ACTIVITY_CLASS)
    }

    @Test
    fun `EXTRA_WIDGET_TARGET has expected key name`() {
        assertEquals("widget_target", WidgetIntents.EXTRA_WIDGET_TARGET)
    }

    @Test
    fun `EXTRA_MYBOOK_ID has expected key name`() {
        assertEquals("mybook_id", WidgetIntents.EXTRA_MYBOOK_ID)
    }

    @Test
    fun `TARGET_BOOK has expected value`() {
        assertEquals("book", WidgetIntents.TARGET_BOOK)
    }

    @Test
    fun `TARGET_HOME has expected value`() {
        assertEquals("home", WidgetIntents.TARGET_HOME)
    }
}
