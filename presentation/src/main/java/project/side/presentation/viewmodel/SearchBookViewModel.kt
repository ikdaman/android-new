package project.side.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // expose only boolean success/failure (null = idle/loading)
    private val _saveState = MutableStateFlow<Boolean?>(null)
    val saveState = _saveState.asStateFlow()

    fun searchBook(title: String) {
        viewModelScope.launch {
            val result = searchBookWithTitleUseCase(title, 0)
            if (result.books.isNotEmpty()) {
                _bookResultListState.value = DomainResult.Success(result.books)
            } else {
                _bookResultListState.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
            }
        }.runCatching {
            _bookResultListState.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
        }
    }

    fun searchBookByIsbn(isbn: String) {
        Log.i("SearchBookViewModel", "searchBookByIsbn: $isbn")
        viewModelScope.launch {
            val result: BookSearchResult = searchBookWithIsbnUseCase(isbn)
            if (result.books.isNotEmpty()) {
                val bookItem = result.books.first()
                _searchedBookDetail.value = DomainResult.Success(bookItem)
                _selectedBookItem.value = bookItem
            } else {
                _searchedBookDetail.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
            }
        }.runCatching {
            _searchedBookDetail.value = DomainResult.Error(message = "책을 찾을 수 없습니다.")
        }
    }

    fun clearSearchedBook() {
        _searchedBookDetail.value = DomainResult.Init
    }

    fun saveSelectedBook(reason: String? = null, startDate: java.time.LocalDate? = null) {
        viewModelScope.launch {
            val book = _selectedBookItem.value
            if (book == null) {
                _saveState.value = false
                return@launch
            }

            val manual = ManualBookInfo(
                title = book.title,
                author = book.author,
                publisher = if (book.publisher.isBlank()) null else book.publisher,
                pubDate = if (book.pubDate.isBlank()) null else book.pubDate,
                isbn = if (book.isbn.isBlank()) null else book.isbn,
                pageCount = book.subInfo?.itemPage
            )
            // attach optional fields if provided (domain model doesn't have them yet)
            // If backend needs reason/startDate, extend ManualBookInfo and Data models accordingly.

            saveManualBookInfoUseCase(manual).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        _saveState.value = result.data
                        // reset after delivering result
                        kotlinx.coroutines.delay(200)
                        _saveState.value = null
                    }
                    is DataResource.Error -> {
                        _saveState.value = false
                        kotlinx.coroutines.delay(200)
                        _saveState.value = null
                    }
                    is DataResource.Loading -> _saveState.value = null
                }
            }
        }
    }
}
