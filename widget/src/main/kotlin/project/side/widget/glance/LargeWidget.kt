package project.side.widget.glance

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.R
import project.side.widget.action.OpenAppAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetUiBook
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.theme.colorsFor
import project.side.widget.theme.ColorVariant

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
    val colors = colorsFor(ColorVariant.WHITE)
    val openApp = actionRunCallback<OpenAppAction>()
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(colors.background))
            .cornerRadius(22.dp)
            .clickable(openApp)
    ) {
        // 헤더는 RemoteViews layout 으로 — Glance Text 의 fontFamily 는 system font 만 지원하고
        // res/font/dunggeunmo.ttf 같은 ttf 는 못 가리키기 때문. AndroidRemoteViews 로 우회한다.
        val context = LocalContext.current
        AndroidRemoteViews(
            remoteViews = RemoteViews(context.packageName, R.layout.widget_large_header),
            modifier = GlanceModifier.fillMaxWidth(),
        )
        Spacer(GlanceModifier.size(4.dp))
        if (books.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "읽고 싶은 책을 추가해 보세요 !",
                    style = TextStyle(
                        color = ColorProvider(colors.text),
                        fontSize = 12.sp,
                    ),
                )
            }
        } else {
            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                items(books, itemId = { it.mybookId.toLong() }) { book ->
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BookHeartIcon(tint = colors.accent, sizeDp = 14)
                        Spacer(GlanceModifier.size(8.dp))
                        Text(
                            text = book.title,
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(colors.text),
                                fontSize = 14.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}
