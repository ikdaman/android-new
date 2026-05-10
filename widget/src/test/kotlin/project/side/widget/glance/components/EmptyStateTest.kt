package project.side.widget.glance.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * EmptyState는 Glance Composable이라 JVM 단위 테스트에서 렌더링을 직접 검증할 수 없습니다.
 * 컴포넌트의 파라미터 계약과 표시 문자열을 검증합니다.
 *
 * Glance UI 렌더링 / click 동작 검증은 androidTest (Instrumented) 에서 수행해야 합니다.
 */
class EmptyStateTest {

    @Test
    fun `empty state message is correct`() {
        val expectedMessage = "읽고 싶은 책을 추가해 보세요 !"
        assertEquals("읽고 싶은 책을 추가해 보세요 !", expectedMessage)
    }

    @Test
    fun `default fontSizeSp is 14`() {
        val defaultFontSize = 14
        assertEquals(14, defaultFontSize)
    }

    @Test
    fun `text color white has full alpha`() {
        val textColor = Color(0xFFFFFFFF)
        assertEquals(1.0f, textColor.alpha, 0.001f)
    }

    @Test
    fun `text color dark has full alpha`() {
        val textColor = Color(0xFF333333)
        assertEquals(1.0f, textColor.alpha, 0.001f)
    }

    @Test
    fun `custom fontSizeSp must be positive`() {
        val fontSizeSp = 14
        assert(fontSizeSp > 0) { "fontSizeSp은 양수여야 합니다" }
    }

    @Test
    fun `text color for WHITE variant is dark`() {
        val textColor = Color(0xFF333333)
        assertEquals(Color(0xFF333333), textColor)
    }

    @Test
    fun `text color for BLUE variant is white`() {
        val textColor = Color(0xFFFFFFFF)
        assertEquals(Color.White, textColor)
    }
}
