package project.side.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.usecase.TestUseCase
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val testUseCase: TestUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(false)
    val uiState = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun checkNickname(nickname: String) {
        viewModelScope.launch {
            testUseCase(nickname).collect {
                when (it) {
                    is DataResource.Success -> {
                        _uiState.value = it.data.available
                        _errorMessage.value = null
                    }

                    is DataResource.Error -> {
                        Log.e("TestViewModel", "checkNickname error: ${it.message}")
                        _errorMessage.value = it.message ?: "닉네임 확인 중 오류가 발생했습니다."
                    }

                    is DataResource.Loading -> {}
                }
            }
        }
    }
}