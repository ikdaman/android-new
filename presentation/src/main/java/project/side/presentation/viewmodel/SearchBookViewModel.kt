package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.model.BookItem
import project.side.domain.model.BookSearchResult
import project.side.domain.model.DomainResult
import project.side.domain.model.ManualBookInfo
import project.side.domain.usecase.SaveManualBookInfoUseCase
import project.side.domain.usecase.search.SearchBookWithIsbnUseCase
import project.side.domain.usecase.search.SearchBookWithTitleUseCase
import javax.inject.Inject

@HiltViewModel
class SearchBookViewModel @Inject constructor(
    private val searchBookWithTitleUseCase: SearchBookWithTitleUseCase,
    private val searchBookWithIsbnUseCase: SearchBookWithIsbnUseCase,
    private val saveManualBookInfoUseCase: SaveManualBookInfoUseCase
) : ViewModel() {


    private val _bookResultListState = MutableStateFlow<DomainResult<List<BookItem>>>(DomainResult.Init)
    val bookResultListState: StateFlow<DomainResult<List<BookItem>>> = _bookResultListState.asStateFlow()

    private val _searchedBookDetail = MutableStateFlow<DomainResult<BookItem>>(DomainResult.Init)
    val bookDetail: StateFlow<DomainResult<BookItem>> = _searchedBookDetail.asStateFlow()

    // selected book item shared between BarcodeScreen and AddBookScreen
    private val _selectedBookItem = MutableStateFlow<BookItem?>(null)
    val selectedBookItem: StateFlow<BookItem?> = _selectedBookItem.asStateFlow()

    private val _saveEvent = MutableSharedFlow<SaveEvent>()
    val saveEvent: SharedFlow<SaveEvent> = _saveEvent.asSharedFlow()

    fun searchBook(title: String) {
        viewModelScope.launch {
            _bookResultListState.value = DomainResult.Loading
            try {
                val result = searchBookWithTitleUseCase(title, 1)
                if (result.books.isNotEmpty()) {
                    _bookResultListState.value = DomainResult.Success(result.books)
                } else {
                    _bookResultListState.value = DomainResult.Error(message = "검색 결과가 없습니다.")
                }
            } catch (e: java.io.IOException) {
                _bookResultListState.value = DomainResult.Error(message = "네트워크 연결을 확인해주세요.")
            } catch (e: Exception) {
                _bookResultListState.value = DomainResult.Error(message = "검색 중 오류가 발생했습니다.")
            }
        }
    }

    fun searchBookByIsbn(isbn: String) {
        viewModelScope.launch {
            _searchedBookDetail.value = DomainResult.Loading
            try {
                val result: BookSearchResult = searchBookWithIsbnUseCase(isbn)
                if (result.books.isNotEmpty()) {
                    val bookItem = result.books.first()
                    _searchedBookDetail.value = DomainResult.Success(bookItem)
                    _selectedBookItem.value = bookItem
                } else {
                    _searchedBookDetail.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
                }
            } catch (e: java.io.IOException) {
                _searchedBookDetail.value = DomainResult.Error(message = "네트워크 연결을 확인해주세요.")
            } catch (e: Exception) {
                _searchedBookDetail.value = DomainResult.Error(message = "책 정보를 불러오지 못했습니다.")
            }
        }
    }

    fun clearSearchedBook() {
        _searchedBookDetail.value = DomainResult.Init
    }

    fun saveSelectedBook(reason: String? = null, startDate: java.time.LocalDate? = null, endDate: java.time.LocalDate? = null) {
        viewModelScope.launch {
            val book = _selectedBookItem.value
            if (book == null) {
                _saveEvent.emit(SaveEvent.Error("선택된 책이 없습니다."))
                return@launch
            }

            val manual = ManualBookInfo(
                title = book.title,
                author = book.author,
                publisher = book.publisher.ifBlank { null },
                pubDate = book.pubDate.ifBlank { null },
                isbn = book.isbn.ifBlank { null },
                pageCount = book.subInfo?.itemPage,
                reason = reason,
                startDate = startDate?.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE),
                endDate = endDate?.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
            )

            saveManualBookInfoUseCase(manual).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        if (result.data) _saveEvent.emit(SaveEvent.Success)
                        else _saveEvent.emit(SaveEvent.Error("책 저장에 실패했어요."))
                    }
                    is DataResource.Error -> {
                        _saveEvent.emit(SaveEvent.Error(result.message ?: "책 저장에 실패했어요."))
                    }
                    is DataResource.Loading -> { /* no-op */ }
                }
            }
        }
    }
}

sealed class SaveEvent {
    data object Success : SaveEvent()
    data class Error(val message: String) : SaveEvent()
}
