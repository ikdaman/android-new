package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.model.BookItem
import project.side.domain.model.BookSearchResult
import project.side.domain.model.DomainResult
import project.side.domain.usecase.search.SearchBookWithIsbnUseCase
import project.side.domain.usecase.search.SearchBookWithTitleUseCase
import javax.inject.Inject

class SearchBookViewModel @Inject constructor(
    private val searchBookWithTitleUseCase: SearchBookWithTitleUseCase,
    private val searchBookWithIsbnUseCase: SearchBookWithIsbnUseCase,
) : ViewModel() {


    private val _bookResultListState = MutableStateFlow<DomainResult<List<BookItem>>>(DomainResult.Init)
    val bookResultListState: StateFlow<DomainResult<List<BookItem>>> = _bookResultListState.asStateFlow()

    private val _bookDetailState = MutableStateFlow<DomainResult<BookSearchResult>>(DomainResult.Init)
    val bookDetail: StateFlow<DomainResult<BookSearchResult>> = _bookDetailState.asStateFlow()


    fun searchBook(title: String) {
        viewModelScope.launch {
            val result = searchBookWithTitleUseCase(title, 0)
            _bookResultListState.value = DomainResult.Success(result.books)
        }.runCatching {
            _bookResultListState.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
        }
    }

    fun searchBookByIsbn(isbn: String) {
        viewModelScope.launch {
            val result: BookSearchResult = searchBookWithIsbnUseCase(isbn)
            _bookDetailState.value = DomainResult.Success(result)
        }.runCatching {
            _bookDetailState.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
        }
    }
}