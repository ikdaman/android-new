package project.side.widget.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class WidgetCacheTest {
    private lateinit var tempDir: File
    private lateinit var cache: WidgetCache

    @Before fun setup() {
        tempDir = createTempDirectory("widget-cache-test").toFile()
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        cache = WidgetCache(store)
    }

    @After fun teardown() { tempDir.deleteRecursively() }

    @Test
    fun `empty cache returns empty list`() = runTest {
        assertTrue(cache.read().isEmpty())
    }

    @Test
    fun `put then read round-trip`() = runTest {
        val books = listOf(
            WidgetUiBook(1, "title1", "reason1", "2026-05-10"),
            WidgetUiBook(2, "title2", null, "2026-05-09"),
        )
        cache.put(books)
        assertEquals(books, cache.read())
    }

    @Test
    fun `put truncates beyond 9 entries`() = runTest {
        val books = (1..15).map { WidgetUiBook(it, "t$it", null, "2026-05-10") }
        cache.put(books)
        assertEquals(9, cache.read().size)
        assertEquals(1, cache.read().first().mybookId)
    }

    @Test
    fun `lastFetchedAt updates on put`() = runTest {
        assertEquals(0L, cache.lastFetchedAt())
        cache.put(listOf(WidgetUiBook(1, "t", null, "2026-05-10")))
        assertTrue(cache.lastFetchedAt() > 0L)
    }
}
