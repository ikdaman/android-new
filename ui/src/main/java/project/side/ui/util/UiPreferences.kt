package project.side.ui.util

import android.content.Context

/**
 * UI 전용 일회성 플래그 보관소 (온보딩 토스트 등).
 * 도메인/저장소 계층과 분리되어 있는 가벼운 SharedPreferences 래퍼.
 */
object UiPreferences {
    private const val FILE = "moabook_ui_prefs"
    private const val KEY_READING_START_HINT_SHOWN = "reading_start_hint_shown"

    fun isReadingStartHintShown(context: Context): Boolean =
        context.applicationContext
            .getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_READING_START_HINT_SHOWN, false)

    fun markReadingStartHintShown(context: Context) {
        context.applicationContext
            .getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_READING_START_HINT_SHOWN, true)
            .apply()
    }
}
