package project.side.widget.data

import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.domain.model.StoreBookItem

class StoreBookMapperTest {
    @Test fun `maps StoreBookItem to WidgetUiBook with all fields`() {
        val item = StoreBookItem(
            mybookId = 99,
            createdDate = "2026-04-01",
            title = "title",
            author = listOf("a"),
            coverImage = "url",
            description = "d",
            reason = "r"
        )
        val expected = WidgetUiBook(99, "title", "r", "2026-04-01")
        assertEquals(expected, item.toWidgetUiBook())
    }

    @Test fun `null reason maps to null`() {
        val item = StoreBookItem(1, "2026-04-01", "t", emptyList(), null, null, null)
        assertEquals(null, item.toWidgetUiBook().reason)
    }
}
