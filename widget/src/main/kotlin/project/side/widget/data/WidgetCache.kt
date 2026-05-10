package project.side.widget.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class WidgetCache(private val store: DataStore<Preferences>) {

    suspend fun read(): List<WidgetUiBook> {
        val raw = store.data.first()[KEY_BOOKS_JSON] ?: return emptyList()
        return runCatching { json.decodeFromString(LIST_SERIALIZER, raw) }.getOrElse { emptyList() }
    }

    suspend fun put(books: List<WidgetUiBook>) {
        val truncated = books.take(MAX_ENTRIES)
        val raw = json.encodeToString(LIST_SERIALIZER, truncated)
        store.edit { prefs ->
            prefs[KEY_BOOKS_JSON] = raw
            prefs[KEY_LAST_FETCHED_AT] = System.currentTimeMillis()
        }
    }

    suspend fun lastFetchedAt(): Long = store.data.first()[KEY_LAST_FETCHED_AT] ?: 0L

    companion object {
        const val MAX_ENTRIES = 9
        private val KEY_BOOKS_JSON = stringPreferencesKey("recent_store_books_json")
        private val KEY_LAST_FETCHED_AT = longPreferencesKey("last_fetched_at")
        private val LIST_SERIALIZER = ListSerializer(WidgetUiBook.serializer())
        private val json = Json { ignoreUnknownKeys = true }
    }
}
