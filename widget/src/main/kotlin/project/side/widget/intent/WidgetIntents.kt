package project.side.widget.intent

import android.content.ComponentName
import android.content.Context
import android.content.Intent

object WidgetIntents {
    const val MAIN_ACTIVITY_CLASS = "project.side.ui.MainActivity"
    const val EXTRA_WIDGET_TARGET = "widget_target"
    const val EXTRA_MYBOOK_ID = "mybook_id"
    const val TARGET_BOOK = "book"
    const val TARGET_HOME = "home"

    fun openBook(context: Context, mybookId: Int): Intent =
        Intent().apply {
            component = ComponentName(context.packageName, MAIN_ACTIVITY_CLASS)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_WIDGET_TARGET, TARGET_BOOK)
            putExtra(EXTRA_MYBOOK_ID, mybookId)
        }

    fun openApp(context: Context): Intent =
        Intent().apply {
            component = ComponentName(context.packageName, MAIN_ACTIVITY_CLASS)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_WIDGET_TARGET, TARGET_HOME)
        }
}
