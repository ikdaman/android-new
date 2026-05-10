package project.side.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetUpdater
import project.side.widget.glance.SmallWidget
import project.side.widget.state.WidgetStateKeys

class RefreshSmallAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RefreshDeps {
        fun cache(): WidgetCache
        fun updater(): WidgetUpdater
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val deps = EntryPointAccessors.fromApplication(context, RefreshDeps::class.java)
        deps.updater().refreshAll()  // server + cache + notify
        val books = deps.cache().read()
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentMybookId = prefs[WidgetStateKeys.SMALL_CURRENT_MYBOOK_ID]
            val pickedBook = RefreshLogic.pickNextByMybookId(books, currentMybookId) {
                kotlin.random.Random.nextInt(it)
            }
            if (pickedBook != null) {
                val newIndex = books.indexOf(pickedBook).coerceAtLeast(0)
                prefs[WidgetStateKeys.SMALL_CURRENT_INDEX] = newIndex
                prefs[WidgetStateKeys.SMALL_CURRENT_MYBOOK_ID] = pickedBook.mybookId
            }
        }
        SmallWidget().update(context, glanceId)
    }
}
