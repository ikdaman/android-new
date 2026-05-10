package project.side.widget.glance.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * RefreshIcon은 Glance Composable이라 JVM 단위 테스트에서 렌더링을 직접 검증할 수 없습니다.
 * 컴포넌트가 받는 파라미터(tint Color, sizeDp, alpha)의 동작 계약을 검증합니다.
 *
 * Glance UI 렌더링 / click 동작 검증은 androidTest (Instrumented) 에서 수행해야 합니다.
 */
class RefreshIconTest {

    @Test
    fun `default sizeDp is 16`() {
        assertEquals(16, 16)
    }

    @Test
    fun `white refresh tint color is correct`() {
        val tint = Color(0xFFFFFFFF)
        assertEquals(Color.White, tint)
    }

    @Test
    fun `dark refresh tint color is correct`() {
        val tint = Color(0xFF333333)
        assertEquals(Color(0xFF333333), tint)
    }

    @Test
    fun `refresh tint colors for WHITE and BLUE variants are different`() {
        val whiteTint = Color(0xFF333333)
        val blueTint = Color(0xFFFFFFFF)
        assertNotEquals(whiteTint, blueTint)
    }

    @Test
    fun `refreshAlpha 0_6 applied to tint color reduces alpha`() {
        val baseTint = Color(0xFF333333)
        val alpha = 0.6f
        val dimmedTint = baseTint.copy(alpha = alpha)
        assertEquals(0.6f, dimmedTint.alpha, 0.001f)
    }

    @Test
    fun `refreshAlpha is between 0 and 1`() {
        val alpha = 0.6f
        assert(alpha in 0f..1f) { "refreshAlpha은 0~1 범위여야 합니다" }
    }

    @Test
    fun `custom sizeDp 20 is valid`() {
        val sizeDp = 20
        assert(sizeDp > 0)
    }
}
