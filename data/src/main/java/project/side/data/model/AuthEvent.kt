package project.side.data.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvent {
    private val _events = MutableSharedFlow<DataAuthEvent>()
    val events = _events.asSharedFlow()

    suspend fun notify(event: DataAuthEvent) {
        _events.emit(event)
    }
}

enum class DataAuthEvent {
    LOGIN_REQUIRED
}
