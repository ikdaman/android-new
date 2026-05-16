package project.side.widget.glance

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.R
import project.side.widget.action.OpenBookAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUiBook
import project.side.widget.domain.DateLabel
import project.side.widget.glance.components.EmptyState
import project.side.widget.glance.theme.colorsFor
import project.side.widget.intent.WidgetIntents
import project.side.widget.receiver.MediumWidgetBlueReceiver
import project.side.widget.receiver.MediumWidgetWhiteReceiver
import project.side.widget.state.WidgetStateKeys
import project.side.widget.theme.ColorVariant

const val ACTION_MEDIUM_PAGE = "project.side.widget.ACTION_MEDIUM_PAGE"
const val EXTRA_TARGET_INDEX = "target_index"

private const val MAX_PAGES = 5

private val MEDIUM_DOT_IDS = intArrayOf(
    R.id.widget_medium_dot_0, R.id.widget_medium_dot_1, R.id.widget_medium_dot_2,
    R.id.widget_medium_dot_3, R.id.widget_medium_dot_4,
)

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
        provideContent { MediumContent(books, variant, appWidgetId) }
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
private fun MediumContent(books: List<WidgetUiBook>, variant: ColorVariant, appWidgetId: Int) {
    val context = LocalContext.current

    if (books.isEmpty()) {
        val colors = colorsFor(variant)
        EmptyState(
            textColor = colors.text,
            onClick = actionStartActivity(WidgetIntents.openApp(context)),
            fontSizeSp = 14,
        )
        return
    }

    val state = currentState<androidx.datastore.preferences.core.Preferences>()
    val rawIndex = state[WidgetStateKeys.MEDIUM_CURRENT_INDEX] ?: 0
    val current = rawIndex.coerceIn(0, books.size - 1)
    val book = books[current]

    val remoteViews = remember(book, variant, current, books.size, appWidgetId) {
        buildMediumRemoteViews(context, book, variant, current, books.size, appWidgetId)
    }
    AndroidRemoteViews(
        remoteViews = remoteViews,
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(
                actionRunCallback<OpenBookAction>(
                    actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                )
            ),
    )
}

private fun buildMediumRemoteViews(
    context: Context,
    book: WidgetUiBook,
    variant: ColorVariant,
    current: Int,
    total: Int,
    appWidgetId: Int,
): RemoteViews {
    val isWhite = variant == ColorVariant.WHITE
    val bgRes = if (isWhite) R.drawable.widget_bg_white else R.drawable.widget_bg_blue
    val iconRes = if (isWhite) R.drawable.ic_book_heart_navy else R.drawable.ic_book_heart_pure
    val textColor = if (isWhite) 0xFF333333.toInt() else 0xFFFFFFFF.toInt()
    val accentColor = if (isWhite) 0xFF010196.toInt() else 0xFFFFFFFF.toInt()
    val dummyColor = if (isWhite) 0xFFA7A7A7.toInt() else 0x99FFFFFF.toInt()
    val activeDot = if (isWhite) R.drawable.dot_white_active else R.drawable.dot_blue_active
    val inactiveDot = if (isWhite) R.drawable.dot_white_inactive else R.drawable.dot_blue_inactive
    val receiverClass = if (isWhite) MediumWidgetWhiteReceiver::class.java else MediumWidgetBlueReceiver::class.java

    return RemoteViews(context.packageName, R.layout.widget_medium_content).apply {
        setInt(R.id.widget_medium_root, "setBackgroundResource", bgRes)
        setImageViewResource(R.id.widget_medium_icon, iconRes)
        setTextViewText(R.id.widget_medium_title, book.title)
        setTextColor(R.id.widget_medium_title, textColor)

        val reasonRaw = book.reason?.takeIf { it.isNotBlank() }
        setTextViewText(R.id.widget_medium_reason, reasonRaw ?: "읽고 싶은 이유를 추가해 주세요.")
        setTextColor(R.id.widget_medium_reason, if (reasonRaw == null) dummyColor else textColor)

        setTextViewText(R.id.widget_medium_date, DateLabel.formatDisplay(book.createdDate))
        setTextColor(R.id.widget_medium_date, accentColor)

        val visibleDots = total.coerceAtMost(MEDIUM_DOT_IDS.size)
        for (i in MEDIUM_DOT_IDS.indices) {
            if (i < visibleDots) {
                setViewVisibility(MEDIUM_DOT_IDS[i], View.VISIBLE)
                setImageViewResource(MEDIUM_DOT_IDS[i], if (i == current) activeDot else inactiveDot)
                val intent = Intent(context, receiverClass).apply {
                    action = ACTION_MEDIUM_PAGE
                    putExtra(EXTRA_TARGET_INDEX, i)
                }
                val pi = PendingIntent.getBroadcast(
                    context,
                    appWidgetId * 100 + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                setOnClickPendingIntent(MEDIUM_DOT_IDS[i], pi)
            } else {
                setViewVisibility(MEDIUM_DOT_IDS[i], View.GONE)
            }
        }

        if (visibleDots > 1) {
            val prevIndex = (current - 1 + visibleDots) % visibleDots
            val nextIndex = (current + 1) % visibleDots
            setOnClickPendingIntent(
                R.id.widget_medium_prev_zone,
                buildPagePendingIntent(context, receiverClass, appWidgetId, prevIndex, code = 50),
            )
            setOnClickPendingIntent(
                R.id.widget_medium_next_zone,
                buildPagePendingIntent(context, receiverClass, appWidgetId, nextIndex, code = 51),
            )
        }
    }
}

private fun buildPagePendingIntent(
    context: Context,
    receiverClass: Class<*>,
    appWidgetId: Int,
    targetIndex: Int,
    code: Int,
): PendingIntent {
    val intent = Intent(context, receiverClass).apply {
        action = ACTION_MEDIUM_PAGE
        putExtra(EXTRA_TARGET_INDEX, targetIndex)
    }
    return PendingIntent.getBroadcast(
        context,
        appWidgetId * 100 + code,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
