package project.side.widget.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
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
import project.side.widget.action.NextAction
import project.side.widget.action.OpenBookAction
import project.side.widget.action.PrevAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUiBook
import project.side.widget.domain.DateLabel
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.components.EmptyState
import project.side.widget.glance.components.PageIndicator
import project.side.widget.glance.theme.colorsFor
import project.side.widget.intent.WidgetIntents
import project.side.widget.receiver.MediumWidgetBlueReceiver
import project.side.widget.receiver.MediumWidgetWhiteReceiver
import project.side.widget.state.WidgetStateKeys
import project.side.widget.theme.ColorVariant

private const val MAX_PAGES = 5

class MediumWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MediumDeps {
        fun cache(): WidgetCache
        fun prefs(): WidgetPreferences
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, MediumDeps::class.java)
        val books = deps.cache().read().take(MAX_PAGES)
        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = manager.getAppWidgetId(id)
        val variant = resolveVariant(context, appWidgetId, deps.prefs())
        provideContent { MediumContent(books, variant) }
    }

    private suspend fun resolveVariant(
        context: Context,
        appWidgetId: Int,
        prefs: WidgetPreferences,
    ): ColorVariant {
        val providerClass = AppWidgetManager.getInstance(context)
            .getAppWidgetInfo(appWidgetId)
            ?.provider
            ?.className
        return when (providerClass) {
            MediumWidgetWhiteReceiver::class.java.name -> ColorVariant.WHITE
            MediumWidgetBlueReceiver::class.java.name -> ColorVariant.BLUE
            else -> prefs.colorFor(appWidgetId)
        }
    }
}

@Composable
private fun MediumContent(books: List<WidgetUiBook>, variant: ColorVariant) {
    val colors = colorsFor(variant)
    val state = currentState<androidx.datastore.preferences.core.Preferences>()
    val rawIndex = state[WidgetStateKeys.MEDIUM_CURRENT_INDEX] ?: 0

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(colors.background))
            .cornerRadius(22.dp)
    ) {
        if (books.isEmpty()) {
            EmptyState(
                textColor = colors.text,
                onClick = actionStartActivity(WidgetIntents.openApp(LocalContext.current)),
                fontSizeSp = 14,
            )
        } else {
            val current = rawIndex.coerceIn(0, books.size - 1)
            val book = books[current]
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BookHeartIcon(tint = colors.accent, sizeDp = 16)
                    Spacer(GlanceModifier.size(6.dp))
                    Text(
                        text = book.title,
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(colors.text),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        modifier = GlanceModifier.defaultWeight().clickable(
                            actionRunCallback<OpenBookAction>(
                                actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                            )
                        ),
                    )
                }
                Spacer(GlanceModifier.size(8.dp))
                val reasonText = book.reason?.takeIf { it.isNotBlank() }
                Text(
                    text = reasonText ?: "읽고 싶은 이유를 추가해 주세요.",
                    maxLines = 3,
                    style = TextStyle(
                        color = ColorProvider(if (reasonText == null) colors.dummyText else colors.text),
                        fontSize = 12.sp,
                    ),
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .clickable(
                            actionRunCallback<OpenBookAction>(
                                actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                            )
                        ),
                )
                Spacer(GlanceModifier.defaultWeight())
                Text(
                    text = DateLabel.formatDisplay(book.createdDate),
                    style = TextStyle(
                        color = ColorProvider(colors.accent),
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
                Spacer(GlanceModifier.size(6.dp))
                Box(
                    modifier = GlanceModifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PageIndicator(
                        total = books.size,
                        current = current,
                        activeColor = colors.indicatorActive,
                        inactiveColor = colors.indicatorInactive,
                        onPrev = actionRunCallback<PrevAction>(),
                        onNext = actionRunCallback<NextAction>(),
                    )
                }
            }
        }
    }
}
