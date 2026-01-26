package project.side.data.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvent {
    private val _events = MutableSharedFlow<DataAuthEvent>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    suspend fun notify(event: DataAuthEvent) {
        _events.tryEmit(event)
    }
}

enum class DataAuthEvent {
    LOGIN_REQUIRED
}
