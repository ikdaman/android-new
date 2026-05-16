package project.side.widget.glance

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.fillMaxSize
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.R
import project.side.widget.action.OpenAppAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetUiBook

private val rowIds = intArrayOf(
    R.id.widget_large_row_0, R.id.widget_large_row_1, R.id.widget_large_row_2,
    R.id.widget_large_row_3, R.id.widget_large_row_4, R.id.widget_large_row_5,
    R.id.widget_large_row_6, R.id.widget_large_row_7, R.id.widget_large_row_8,
)
private val titleIds = intArrayOf(
    R.id.widget_large_title_0, R.id.widget_large_title_1, R.id.widget_large_title_2,
    R.id.widget_large_title_3, R.id.widget_large_title_4, R.id.widget_large_title_5,
    R.id.widget_large_title_6, R.id.widget_large_title_7, R.id.widget_large_title_8,
)

class LargeWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LargeDeps {
        fun cache(): WidgetCache
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, LargeDeps::class.java)
        val books = deps.cache().read().take(9)
        provideContent { LargeContent(books) }
    }
}

@Composable
private fun LargeContent(books: List<WidgetUiBook>) {
    val context = LocalContext.current
    val remoteViews = remember(books) {
        RemoteViews(context.packageName, R.layout.widget_large_content).apply {
            if (books.isEmpty()) {
                setViewVisibility(R.id.widget_large_empty, View.VISIBLE)
                setViewVisibility(R.id.widget_large_list, View.GONE)
            } else {
                setViewVisibility(R.id.widget_large_empty, View.GONE)
                setViewVisibility(R.id.widget_large_list, View.VISIBLE)
                for (i in 0 until 9) {
                    if (i < books.size) {
                        setViewVisibility(rowIds[i], View.VISIBLE)
                        setTextViewText(titleIds[i], books[i].title)
                    } else {
                        setViewVisibility(rowIds[i], View.GONE)
                    }
                }
            }
        }
    }
    AndroidRemoteViews(
        remoteViews = remoteViews,
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionRunCallback<OpenAppAction>()),
    )
}
