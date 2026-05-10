package project.side.widget.action

import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.widget.data.WidgetUiBook

class RefreshLogicTest {
    private fun books(n: Int) = (1..n).map { WidgetUiBook(it, "t$it", null, "2026-05-10") }

    @Test fun `empty returns -2`() =
        assertEquals(-2, RefreshLogic.pickNextIndex(emptyList(), 0) { 0 })

    @Test fun `single returns -1`() =
        assertEquals(-1, RefreshLogic.pickNextIndex(books(1), 0) { 0 })

    @Test fun `two excludes current`() =
        assertEquals(1, RefreshLogic.pickNextIndex(books(2), 0) { 0 })

    @Test fun `nine excludes current and uses random for selection`() =
        assertEquals(5, RefreshLogic.pickNextIndex(books(9), 4) { 4 })
}
