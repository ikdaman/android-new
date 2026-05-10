package project.side.widget.data

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import project.side.widget.glance.MediumWidget
import project.side.widget.glance.SmallWidget

@Singleton
class GlanceWidgetUpdateNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetUpdateNotifier {
    override suspend fun notifyAllWidgets() {
        SmallWidget().updateAll(context)
        MediumWidget().updateAll(context)
        // LargeWidget will be added in Task 14.
    }
}
