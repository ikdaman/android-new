package project.side.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.model.MyBookDetail
import project.side.domain.usecase.mybook.DeleteMyBookUseCase
import project.side.domain.usecase.mybook.GetMyBookDetailUseCase
import project.side.presentation.util.SnackbarManager
import javax.inject.Inject

@HiltViewModel
class BookInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMyBookDetailUseCase: GetMyBookDetailUseCase,
    private val deleteMyBookUseCase: DeleteMyBookUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<BookInfoUiState> = MutableStateFlow(BookInfoUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess = _deleteSuccess.asStateFlow()

    private val mybookId: Int = savedStateHandle["mybookId"] ?: -1

    init {
        if (mybookId != -1) {
            fetchDetail(mybookId)
        } else {
            _uiState.value = BookInfoUiState.Error("잘못된 책 정보입니다.")
        }
    }

    fun fetchDetail(mybookId: Int) {
        viewModelScope.launch {
            getMyBookDetailUseCase(mybookId).collect {
                when (it) {
                    is DataResource.Success -> {
                        _uiState.value = BookInfoUiState.Success(it.data)
                    }
                    is DataResource.Error -> {
                        _uiState.value = BookInfoUiState.Error(it.message)
                    }
                    is DataResource.Loading -> {
                        _uiState.value = BookInfoUiState.Loading
                    }
                }
            }
        }
    }

    fun deleteBook() {
        if (mybookId == -1) return
        viewModelScope.launch {
            deleteMyBookUseCase(mybookId).collect {
                when (it) {
                    is DataResource.Success -> {
                        SnackbarManager.show("책을 정리했어요.")
                        _deleteSuccess.value = true
                    }
                    is DataResource.Error -> {
                        SnackbarManager.show(it.message ?: "삭제에 실패했어요.")
                    }
                    is DataResource.Loading -> { /* no-op */ }
                }
            }
        }
    }
}

sealed class BookInfoUiState {
    data object Loading : BookInfoUiState()
    data class Success(val detail: MyBookDetail) : BookInfoUiState()
    data class Error(val message: String?) : BookInfoUiState()
}
