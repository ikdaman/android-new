package project.side.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetUpdater
import project.side.widget.glance.ACTION_REFRESH_SMALL
import project.side.widget.glance.SmallWidget

@AndroidEntryPoint
class SmallWidgetBlueReceiver : GlanceAppWidgetReceiver() {
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
