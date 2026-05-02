package project.side.ui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpacingTest {

    @Test
    fun `Spacing 토큰은 4pt 그리드를 따른다`() {
        listOf(
            Spacing.xs,
            Spacing.sm,
            Spacing.md,
            Spacing.lg,
            Spacing.xl,
            Spacing.xxl,
        ).forEach { value ->
            val px = value.value
            assertTrue("$value 는 4pt 단위여야 한다", (px % 4f) == 0f)
        }
    }

    @Test
    fun `Spacing 토큰은 단조 증가한다`() {
        val ordered = listOf(
            Spacing.xs,
            Spacing.sm,
            Spacing.md,
            Spacing.lg,
            Spacing.xl,
            Spacing.xxl,
        )
        ordered.zipWithNext().forEach { (a, b) ->
            assertTrue("$a < $b 여야 한다", a.value < b.value)
        }
    }

    @Test
    fun `Spacing 토큰의 기본값`() {
        assertEquals(4.dp, Spacing.xs)
        assertEquals(8.dp, Spacing.sm)
        assertEquals(16.dp, Spacing.md)
        assertEquals(24.dp, Spacing.lg)
        assertEquals(40.dp, Spacing.xl)
        assertEquals(64.dp, Spacing.xxl)
    }
}
