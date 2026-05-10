package project.side.widget.glance.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.widget.theme.ColorVariant

class WidgetThemeTest {

    // ── WHITE variant ────────────────────────────────────────────────────────

    @Test
    fun `colorsFor WHITE returns correct background`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFFF6F9FF), colors.background)
    }

    @Test
    fun `colorsFor WHITE returns correct text`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFF333333), colors.text)
    }

    @Test
    fun `colorsFor WHITE returns correct accent`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFF010196), colors.accent)
    }

    @Test
    fun `colorsFor WHITE returns correct dummyText`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFFA7A7A7), colors.dummyText)
    }

    @Test
    fun `colorsFor WHITE returns correct indicatorActive`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFF747474), colors.indicatorActive)
    }

    @Test
    fun `colorsFor WHITE returns correct indicatorInactive`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFFD9D9D9), colors.indicatorInactive)
    }

    @Test
    fun `colorsFor WHITE returns correct refreshTint`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(Color(0xFF333333), colors.refreshTint)
    }

    @Test
    fun `colorsFor WHITE returns refreshAlpha 0_6`() {
        val colors = colorsFor(ColorVariant.WHITE)
        assertEquals(0.6f, colors.refreshAlpha)
    }

    // ── BLUE variant ─────────────────────────────────────────────────────────

    @Test
    fun `colorsFor BLUE returns correct background`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFF010196), colors.background)
    }

    @Test
    fun `colorsFor BLUE returns white text`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFFFFFFFF), colors.text)
    }

    @Test
    fun `colorsFor BLUE returns white accent`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFFFFFFFF), colors.accent)
    }

    @Test
    fun `colorsFor BLUE dummyText is white with 60pct alpha`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFFFFFFFF).copy(alpha = 0.6f), colors.dummyText)
    }

    @Test
    fun `colorsFor BLUE indicatorActive is white`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFFFFFFFF), colors.indicatorActive)
    }

    @Test
    fun `colorsFor BLUE indicatorInactive is white with 30pct alpha`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFFFFFFFF).copy(alpha = 0.3f), colors.indicatorInactive)
    }

    @Test
    fun `colorsFor BLUE refreshTint is white`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(Color(0xFFFFFFFF), colors.refreshTint)
    }

    @Test
    fun `colorsFor BLUE returns refreshAlpha 0_6`() {
        val colors = colorsFor(ColorVariant.BLUE)
        assertEquals(0.6f, colors.refreshAlpha)
    }

    // ── WidgetColors data class ──────────────────────────────────────────────

    @Test
    fun `WidgetColors copy produces independent instance`() {
        val original = colorsFor(ColorVariant.WHITE)
        val copy = original.copy(refreshAlpha = 1.0f)
        assertEquals(1.0f, copy.refreshAlpha)
        assertEquals(0.6f, original.refreshAlpha)
    }

    @Test
    fun `WHITE and BLUE produce distinct instances`() {
        val white = colorsFor(ColorVariant.WHITE)
        val blue = colorsFor(ColorVariant.BLUE)
        assert(white != blue)
    }
}
