package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase
): ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = getLoginStateUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents = _snackbarEvents.asSharedFlow()

    init {
        validateToken()
    }

    private fun validateToken() {
        viewModelScope.launch {
            val loggedIn = isLoggedIn.first { it }
            if (loggedIn) {
                getMyInfoUseCase().collect { result ->
                    if (result is DataResource.Success) {
                        _nickname.value = result.data.nickname
                    }
                }
            }
        }
    }

    suspend fun showSnackbar(message: String) {
        _snackbarEvents.emit(message)
    }
}
