package project.side.widget.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import project.side.widget.theme.ColorVariant

class WidgetPreferences(private val store: DataStore<Preferences>) {

    suspend fun colorFor(appWidgetId: Int): ColorVariant {
        val data = store.data.first()
        val key = colorKey(appWidgetId)
        val saved = data[key]
        if (saved != null) return ColorVariant.fromName(saved)
        val default = data[KEY_LAST_DEFAULT]
        return ColorVariant.fromName(default)
    }

    suspend fun setColor(appWidgetId: Int, variant: ColorVariant) {
        store.edit { prefs ->
            prefs[colorKey(appWidgetId)] = variant.name
            prefs[KEY_LAST_DEFAULT] = variant.name
        }
    }

    suspend fun clear(appWidgetId: Int) {
        store.edit { prefs ->
            prefs.remove(colorKey(appWidgetId))
        }
    }

    private fun colorKey(appWidgetId: Int) =
        stringPreferencesKey("widget_color_$appWidgetId")

    companion object {
        private val KEY_LAST_DEFAULT = stringPreferencesKey("widget_color_last_default")
    }
}
