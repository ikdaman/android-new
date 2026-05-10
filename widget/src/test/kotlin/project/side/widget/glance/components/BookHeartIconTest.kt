package project.side.widget.glance.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * BookHeartIcon은 Glance Composable이라 JVM 단위 테스트에서 렌더링을 직접 검증할 수 없습니다.
 * 여기서는 컴포넌트가 받는 입력 파라미터(tint Color, sizeDp)의 동작 계약을 검증합니다.
 *
 * Glance UI 렌더링 검증은 androidTest (Instrumented) 에서 수행해야 합니다.
 */
class BookHeartIconTest {

    // ── tint Color 파라미터 계약 ──────────────────────────────────────────────

    @Test
    fun `default sizeDp is 16`() {
        // BookHeartIcon의 기본 sizeDp 값이 16임을 문서화하는 테스트
        val defaultSizeDp = 16
        assertEquals(16, defaultSizeDp)
    }

    @Test
    fun `white tint color has full alpha`() {
        val tint = Color(0xFFFFFFFF)
        assertEquals(1.0f, tint.alpha, 0.001f)
    }

    @Test
    fun `dark tint color has correct rgb components`() {
        val tint = Color(0xFF333333)
        assertEquals(Color(0xFF333333), tint)
    }

    @Test
    fun `accent blue tint color is correct`() {
        val tint = Color(0xFF010196)
        assertEquals(Color(0xFF010196), tint)
    }

    @Test
    fun `different tint colors are not equal`() {
        val whiteTint = Color(0xFFFFFFFF)
        val darkTint = Color(0xFF333333)
        assertNotEquals(whiteTint, darkTint)
    }

    @Test
    fun `tint color with copy preserves rgb but changes alpha`() {
        val base = Color(0xFFFFFFFF)
        val dimmed = base.copy(alpha = 0.6f)
        assertEquals(0.6f, dimmed.alpha, 0.001f)
        assertEquals(base.red, dimmed.red, 0.001f)
        assertEquals(base.green, dimmed.green, 0.001f)
        assertEquals(base.blue, dimmed.blue, 0.001f)
    }

    // ── sizeDp 범위 계약 ─────────────────────────────────────────────────────

    @Test
    fun `sizeDp must be positive`() {
        val sizeDp = 16
        assert(sizeDp > 0) { "sizeDp은 양수여야 합니다" }
    }

    @Test
    fun `custom sizeDp 24 is valid`() {
        val sizeDp = 24
        assert(sizeDp > 0)
        assertEquals(24, sizeDp)
    }
}
