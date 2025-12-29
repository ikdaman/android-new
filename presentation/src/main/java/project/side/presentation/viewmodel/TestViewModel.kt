package project.side.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.usecase.TestUseCase
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val testUseCase: TestUseCase
) : ViewModel() {
    val uiState = MutableStateFlow(false)

    fun checkNickname(nickname: String) {
        viewModelScope.launch {
            testUseCase(nickname).collect {
                when (it) {
                    is DataResource.Success -> {
                        uiState.value = it.data.available
                    }

                    is DataResource.Error -> {
                        Log.d("yewon", "$it")
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }
}