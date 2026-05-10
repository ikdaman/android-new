package project.side.widget.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorVariantTest {
    @Test
    fun `fromName returns WHITE for null`() {
        assertEquals(ColorVariant.WHITE, ColorVariant.fromName(null))
    }

    @Test
    fun `fromName returns WHITE for unknown name`() {
        assertEquals(ColorVariant.WHITE, ColorVariant.fromName("RED"))
    }

    @Test
    fun `fromName returns BLUE for BLUE`() {
        assertEquals(ColorVariant.BLUE, ColorVariant.fromName("BLUE"))
    }

    @Test
    fun `fromName returns WHITE for WHITE`() {
        assertEquals(ColorVariant.WHITE, ColorVariant.fromName("WHITE"))
    }
}
