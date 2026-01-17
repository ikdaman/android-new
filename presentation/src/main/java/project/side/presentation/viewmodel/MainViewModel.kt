package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import project.side.domain.usecase.GetLoginStateUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase
): ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = getLoginStateUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
}