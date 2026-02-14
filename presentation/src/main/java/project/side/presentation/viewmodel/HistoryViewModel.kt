package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.usecase.GetHistoryBooksUseCase
import project.side.presentation.model.HistoryBookState
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryBooksUseCase: GetHistoryBooksUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<HistoryBookState> = MutableStateFlow(HistoryBookState())
    val uiState = _uiState.asStateFlow()

//    init {
//        getBooks()
//    }

    fun getBooks(
        keyword: String? = null,
        page: Int? = 0,
        size: Int? = 30,
        isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            getHistoryBooksUseCase(keyword, page, size).collect {
                when (it) {
                    is DataResource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            totalPages = it.data.totalPages,
                            nowPage = it.data.nowPage,
                            books = if (isLoadMore) _uiState.value.books + it.data.books else it.data.books,
                        )
                    }

                    is DataResource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is DataResource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun onViewTypeChanged() {
        _uiState.value = _uiState.value.copy(viewType = _uiState.value.viewType.toggle())
    }
}