package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import project.side.domain.model.DomainAuthEvent
import project.side.domain.usecase.GetAuthEventUseCase
import project.side.domain.usecase.GetLoginStateUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthEventUseCase: GetAuthEventUseCase,
    getLoginStateUseCase: GetLoginStateUseCase
) : ViewModel() {

    private val _authEvent = MutableSharedFlow<DomainAuthEvent>()
    val authEvent: SharedFlow<DomainAuthEvent> = _authEvent.asSharedFlow()

    val isLoggedIn: StateFlow<Boolean> = getLoginStateUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            getAuthEventUseCase().collect { event ->
                _authEvent.emit(event)
            }
        }
    }
}
