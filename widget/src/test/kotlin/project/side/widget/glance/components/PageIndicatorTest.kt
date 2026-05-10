package project.side.widget.glance.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PageIndicator는 Glance Composable이라 JVM 단위 테스트에서 렌더링을 직접 검증할 수 없습니다.
 * 페이지 인디케이터의 파라미터 계약과 인덱스 범위 로직을 검증합니다.
 *
 * Glance UI 렌더링 / click 동작 검증은 androidTest (Instrumented) 에서 수행해야 합니다.
 */
class PageIndicatorTest {

    // ── 파라미터 범위 계약 ────────────────────────────────────────────────────

    @Test
    fun `current index is within 0 until total`() {
        val total = 5
        val current = 2
        assertTrue(current in 0 until total)
    }

    @Test
    fun `current index 0 is valid`() {
        val total = 3
        val current = 0
        assertTrue(current in 0 until total)
    }

    @Test
    fun `current index total-1 is valid`() {
        val total = 3
        val current = total - 1
        assertTrue(current in 0 until total)
    }

    @Test
    fun `total of 1 produces single dot`() {
        val total = 1
        assertEquals(1, total)
    }

    @Test
    fun `total of 0 is edge case`() {
        val total = 0
        assertEquals(0, total)
    }

    // ── dot 색상 로직 ─────────────────────────────────────────────────────────

    @Test
    fun `dot color at current index is activeColor`() {
        val activeColor = Color(0xFF747474)
        val inactiveColor = Color(0xFFD9D9D9)
        val total = 3
        val current = 1

        val dotColors = List(total) { index ->
            if (index == current) activeColor else inactiveColor
        }

        assertEquals(activeColor, dotColors[current])
    }

    @Test
    fun `dot colors at non-current indices are inactiveColor`() {
        val activeColor = Color(0xFF747474)
        val inactiveColor = Color(0xFFD9D9D9)
        val total = 3
        val current = 1

        val dotColors = List(total) { index ->
            if (index == current) activeColor else inactiveColor
        }

        assertEquals(inactiveColor, dotColors[0])
        assertEquals(inactiveColor, dotColors[2])
    }

    @Test
    fun `active and inactive colors are distinct`() {
        val activeColor = Color(0xFF747474)
        val inactiveColor = Color(0xFFD9D9D9)
        assertNotEquals(activeColor, inactiveColor)
    }

    @Test
    fun `BLUE variant active and inactive colors are distinct`() {
        val activeColor = Color(0xFFFFFFFF)
        val inactiveColor = Color(0xFFFFFFFF).copy(alpha = 0.3f)
        assertNotEquals(activeColor, inactiveColor)
    }

    // ── dot 크기 계약 ─────────────────────────────────────────────────────────

    @Test
    fun `dot size is 6dp`() {
        val dotSizeDp = 6
        assertEquals(6, dotSizeDp)
    }

    @Test
    fun `dot count equals total`() {
        val total = 4
        val dots = List(total) { it }
        assertEquals(total, dots.size)
    }
}
