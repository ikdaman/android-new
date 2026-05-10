package project.side.widget.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.action.OpenAppAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUiBook
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.theme.colorsFor
import project.side.widget.theme.ColorVariant

class LargeWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LargeDeps {
        fun cache(): WidgetCache
        fun prefs(): WidgetPreferences
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, LargeDeps::class.java)
        val books = deps.cache().read().take(9)
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val variant = deps.prefs().colorFor(appWidgetId)
        provideContent { LargeContent(books, variant) }
    }
}

@Composable
private fun LargeContent(books: List<WidgetUiBook>, variant: ColorVariant) {
    val colors = colorsFor(variant)
    val openApp = actionRunCallback<OpenAppAction>()
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(colors.background))
            .cornerRadius(22.dp)
            .clickable(openApp)
    ) {
        // Header bar
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(headerBarColor(variant)))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "BOOK NAME",
                style = TextStyle(
                    color = ColorProvider(if (variant == ColorVariant.WHITE) Color(0xFF333333) else Color(0xFF010196)),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
            )
        }
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

private fun headerBarColor(variant: ColorVariant): Color = when (variant) {
    ColorVariant.WHITE -> Color(0xFFD4D4D4)
    // L 파란 변형 헤더 색은 spec §14의 미해결 항목. 잠정값 흰색.
    ColorVariant.BLUE -> Color(0xFFFFFFFF)
}
