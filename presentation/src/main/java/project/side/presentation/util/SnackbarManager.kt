package project.side.presentation.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarManager {
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    suspend fun show(message: String) {
        _events.emit(message)
    }
}
