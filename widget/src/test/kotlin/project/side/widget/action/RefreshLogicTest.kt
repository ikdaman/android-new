package project.side.widget.action

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

    @Test fun `pickNextByMybookId empty returns null`() =
        assertNull(RefreshLogic.pickNextByMybookId(emptyList(), 1) { 0 })

    @Test fun `pickNextByMybookId single returns that book`() =
        assertEquals(1, RefreshLogic.pickNextByMybookId(books(1), 1) { 0 }?.mybookId)

    @Test fun `pickNextByMybookId excludes current id`() {
        val list = books(3)  // ids 1,2,3
        val picked = RefreshLogic.pickNextByMybookId(list, 2) { 0 }
        assertEquals(1, picked?.mybookId)  // candidates = [1,3], randomInt(2)=0 → 1
    }

    @Test fun `pickNextByMybookId currentId not in books picks random`() {
        val list = books(3)
        val picked = RefreshLogic.pickNextByMybookId(list, 999) { 1 }
        // candidates = [1,2,3], randomInt(3)=1 → mybookId 2
        assertEquals(2, picked?.mybookId)
    }
}
