package project.side.widget.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import project.side.domain.DataResource
import project.side.domain.repository.MyBookRepository

@Singleton
class WidgetUpdaterImpl @Inject constructor(
    private val repository: MyBookRepository,
    private val cache: WidgetCache,
    private val notifier: WidgetUpdateNotifier,
) : WidgetUpdater {

    override suspend fun refreshAll() {
        try {
            val terminal = repository.getStoreBooks(null, 0, 9, "createdDate,desc")
                .first { it !is DataResource.Loading }
            if (terminal is DataResource.Success) {
                cache.put(terminal.data.content.map { it.toWidgetUiBook() })
            }
            // Error → keep stale cache (do not throw)
        } finally {
            notifier.notifyAllWidgets()
        }
    }
}
