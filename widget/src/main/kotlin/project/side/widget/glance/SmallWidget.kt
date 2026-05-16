package project.side.widget.glance

import android.content.Context
import android.appwidget.AppWidgetManager
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
import androidx.glance.appwidget.SizeMode
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
import project.side.widget.action.OpenBookAction
import project.side.widget.action.RefreshSmallAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUiBook
import project.side.widget.domain.DateLabel
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.components.EmptyState
import project.side.widget.glance.components.RefreshIcon
import project.side.widget.glance.theme.colorsFor
import project.side.widget.intent.WidgetIntents
import project.side.widget.receiver.SmallWidgetBlueReceiver
import project.side.widget.receiver.SmallWidgetWhiteReceiver
import project.side.widget.state.WidgetStateKeys
import project.side.widget.theme.ColorVariant

class SmallWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SmallDeps {
        fun cache(): WidgetCache
        fun prefs(): WidgetPreferences
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, SmallDeps::class.java)
        val books = deps.cache().read()
        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = manager.getAppWidgetId(id)
        val variant = resolveVariant(context, appWidgetId, deps.prefs())
        // SMALL_CURRENT_MYBOOK_ID 미설정 시 첫 책으로 초기화
        if (books.isNotEmpty()) {
            androidx.glance.appwidget.state.updateAppWidgetState(context, id) { prefs ->
                if (prefs[WidgetStateKeys.SMALL_CURRENT_MYBOOK_ID] == null) {
                    val safeIndex = (prefs[WidgetStateKeys.SMALL_CURRENT_INDEX] ?: 0)
                        .coerceIn(0, books.size - 1)
                    prefs[WidgetStateKeys.SMALL_CURRENT_MYBOOK_ID] = books[safeIndex].mybookId
                    prefs[WidgetStateKeys.SMALL_CURRENT_INDEX] = safeIndex
                }
            }
        }
        provideContent { SmallContent(books, variant) }
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
            SmallWidgetWhiteReceiver::class.java.name -> ColorVariant.WHITE
            SmallWidgetBlueReceiver::class.java.name -> ColorVariant.BLUE
            else -> prefs.colorFor(appWidgetId)
        }
    }
}

@Composable
private fun SmallContent(books: List<WidgetUiBook>, variant: ColorVariant) {
    val colors = colorsFor(variant)
    val state = currentState<androidx.datastore.preferences.core.Preferences>()
    val rawIndex = state[WidgetStateKeys.SMALL_CURRENT_INDEX] ?: 0
    val safeIndex = if (books.isNotEmpty()) rawIndex.coerceIn(0, books.size - 1) else 0

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
            val book = books[safeIndex]
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clickable(
                        actionRunCallback<OpenBookAction>(
                            actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                        )
                    ),
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BookHeartIcon(tint = colors.accent, sizeDp = 16)
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    RefreshIcon(
                        tint = colors.refreshTint.copy(alpha = colors.refreshAlpha),
                        onClick = actionRunCallback<RefreshSmallAction>(),
                        sizeDp = 16,
                    )
                }
                Spacer(modifier = GlanceModifier.size(8.dp))
                Text(
                    text = book.title,
                    maxLines = 2,
                    style = TextStyle(
                        color = ColorProvider(colors.text),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = DateLabel.format(book.createdDate),
                    style = TextStyle(
                        color = ColorProvider(colors.accent),
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
            }
        }
    }
}
