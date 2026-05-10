package project.side.widget.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlanceWidgetUpdateNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetUpdateNotifier {
    override suspend fun notifyAllWidgets() {
        // Filled in Task 12/13/14 once each GlanceAppWidget exists.
        // Each widget calls .updateAll(context).
    }
}
