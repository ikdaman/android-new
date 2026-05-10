package project.side.widget.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.StoreBook
import project.side.domain.model.StoreBookItem
import project.side.domain.repository.MyBookRepository

class WidgetUpdaterImplTest {

    @Test
    fun `refreshAll caches mapped books on success`() = runTest {
        val repo = mockk<MyBookRepository>()
        val cache = mockk<WidgetCache>(relaxed = true)
        val notifier = mockk<WidgetUpdateNotifier>(relaxed = true)
        val items = listOf(
            StoreBookItem(1, "2026-05-10", "t1", listOf("a"), null, null, "r1"),
            StoreBookItem(2, "2026-05-09", "t2", listOf("a"), null, null, null),
        )
        coEvery { repo.getStoreBooks(null, 0, 9, "createdDate,desc") } returns
            flowOf(DataResource.Success(StoreBook(items, 1, 2, true, true, 9, 0, 2, false)))

        val captured = slot<List<WidgetUiBook>>()
        coEvery { cache.put(capture(captured)) } returns Unit

        WidgetUpdaterImpl(repo, cache, notifier).refreshAll()

        assertEquals(2, captured.captured.size)
        assertEquals("t1", captured.captured[0].title)
        coVerify { notifier.notifyAllWidgets() }
    }

    @Test
    fun `refreshAll on Error keeps stale cache and still notifies`() = runTest {
        val repo = mockk<MyBookRepository>()
        val cache = mockk<WidgetCache>(relaxed = true)
        val notifier = mockk<WidgetUpdateNotifier>(relaxed = true)
        coEvery { repo.getStoreBooks(any(), any(), any(), any()) } returns
            flowOf(DataResource.Error("boom", 500))

        WidgetUpdaterImpl(repo, cache, notifier).refreshAll()

        coVerify(exactly = 0) { cache.put(any()) }
        coVerify { notifier.notifyAllWidgets() }
    }
}
