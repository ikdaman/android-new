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
import project.side.widget.data.WidgetCache
import project.side.widget.glance.MediumWidget
import project.side.widget.state.WidgetStateKeys

class PrevAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PrevDeps { fun cache(): WidgetCache }

    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters,
    ) {
        val total = EntryPointAccessors.fromApplication(context, PrevDeps::class.java)
            .cache().read().take(5).size
        if (total == 0) return
        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[WidgetStateKeys.MEDIUM_CURRENT_INDEX] ?: 0
            prefs[WidgetStateKeys.MEDIUM_CURRENT_INDEX] = ((current - 1) % total + total) % total
        }
        MediumWidget().update(context, glanceId)
    }
}
