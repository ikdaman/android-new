package project.side.presentation.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarManager {
    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    suspend fun show(message: String) {
        _events.send(message)
    }
}
