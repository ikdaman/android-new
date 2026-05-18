package project.side.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import project.side.widget.action.RefreshLogic
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetUpdater
import project.side.widget.glance.ACTION_REFRESH_SMALL
import project.side.widget.glance.SmallWidget
import project.side.widget.state.WidgetStateKeys

@AndroidEntryPoint
class SmallWidgetWhiteReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmallWidget()

    @Inject lateinit var updater: WidgetUpdater
    @Inject lateinit var cache: WidgetCache
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val pendingResult = goAsync()
        scope.launch {
            try {
                glanceAppWidget.updateAll(context)
                updater.refreshAll()
            } finally {
                pendingResult?.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_SMALL) {
            val pendingResult = goAsync()
            scope.launch {
                try {
                    handleRefresh(context, updater, cache)
                } finally {
                    pendingResult?.finish()
                }
            }
        }
    }
}

internal suspend fun handleRefresh(
    context: Context,
    updater: WidgetUpdater,
    cache: WidgetCache,
) {
    updater.refreshAll()
    val books = cache.read()
    val manager = GlanceAppWidgetManager(context)
    val widget = SmallWidget()
    val glanceIds = manager.getGlanceIds(SmallWidget::class.java)
    glanceIds.forEach { gid ->
        updateAppWidgetState(context, gid) { prefs ->
            val currentMybookId = prefs[WidgetStateKeys.SMALL_CURRENT_MYBOOK_ID]
            val pickedBook = RefreshLogic.pickNextByMybookId(books, currentMybookId) {
                Random.nextInt(it)
            }
            if (pickedBook != null) {
                val newIndex = books.indexOf(pickedBook).coerceAtLeast(0)
                prefs[WidgetStateKeys.SMALL_CURRENT_INDEX] = newIndex
                prefs[WidgetStateKeys.SMALL_CURRENT_MYBOOK_ID] = pickedBook.mybookId
            }
        }
        widget.update(context, gid)
    }
}
