package project.side.widget.data

import android.util.Log
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
        Log.d(TAG, "refreshAll() start")
        try {
            val terminal = runCatching {
                repository.getStoreBooks(null, 0, 9, "createdAt,desc")
                    .first { it !is DataResource.Loading }
            }.getOrElse { throwable ->
                Log.e(TAG, "getStoreBooks threw", throwable)
                DataResource.Error(throwable.message ?: "unknown")
            }
            when (terminal) {
                is DataResource.Success -> {
                    val count = terminal.data.content.size
                    Log.d(TAG, "fetch success: $count books")
                    cache.put(terminal.data.content.map { it.toWidgetUiBook() })
                }
                is DataResource.Error -> {
                    Log.w(TAG, "fetch error: ${terminal.message} — keeping stale cache")
                }
                is DataResource.Loading -> {
                    Log.w(TAG, "fetch ended on Loading (unexpected)")
                }
            }
        } finally {
            notifier.notifyAllWidgets()
            Log.d(TAG, "refreshAll() done")
        }
    }

    private companion object {
        const val TAG = "WidgetUpdater"
    }
}
