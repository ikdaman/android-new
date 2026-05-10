package project.side.widget.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetUiBookTest {
    @Test
    fun `serialize and deserialize round-trip preserves all fields`() {
        val original = WidgetUiBook(
            mybookId = 42,
            title = "자본주의 시대에서 살아남기",
            reason = "경제 유튜브 슈카에서 추천",
            createdDate = "2026-03-03"
        )
        val json = Json.encodeToString(WidgetUiBook.serializer(), original)
        val decoded = Json.decodeFromString(WidgetUiBook.serializer(), json)
        assertEquals(original, decoded)
    }

    @Test
    fun `nullable reason serializes correctly when null`() {
        val original = WidgetUiBook(mybookId = 1, title = "t", reason = null, createdDate = "2026-01-01")
        val json = Json.encodeToString(WidgetUiBook.serializer(), original)
        val decoded = Json.decodeFromString(WidgetUiBook.serializer(), json)
        assertEquals(original, decoded)
    }
}
