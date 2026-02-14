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
import project.side.presentation.model.SearchBookState
import javax.inject.Inject

private const val PAGE_SIZE = 50

@HiltViewModel
class SearchBookViewModel @Inject constructor(
    private val searchBookWithTitleUseCase: SearchBookWithTitleUseCase,
    private val searchBookWithIsbnUseCase: SearchBookWithIsbnUseCase,
    private val saveManualBookInfoUseCase: SaveManualBookInfoUseCase
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchBookState())
    val searchState: StateFlow<SearchBookState> = _searchState.asStateFlow()

    private val _searchedBookDetail = MutableStateFlow<DomainResult<BookItem>>(DomainResult.Init)
    val bookDetail: StateFlow<DomainResult<BookItem>> = _searchedBookDetail.asStateFlow()

    // selected book item shared between BarcodeScreen and AddBookScreen
    private val _selectedBookItem = MutableStateFlow<BookItem?>(null)
    val selectedBookItem: StateFlow<BookItem?> = _selectedBookItem.asStateFlow()

    private val _saveEvent = MutableSharedFlow<SaveEvent>()
    val saveEvent: SharedFlow<SaveEvent> = _saveEvent.asSharedFlow()

    fun searchBook(title: String) {
        viewModelScope.launch {
            _searchState.value = SearchBookState(query = title, isLoading = true)
            try {
                val result = searchBookWithTitleUseCase(title, 1)
                val hasMore = 1 * PAGE_SIZE < result.totalBookCount
                _searchState.value = _searchState.value.copy(
                    books = result.books,
                    currentPage = 1,
                    totalBookCount = result.totalBookCount,
                    isLoading = false,
                    hasMore = hasMore,
                    errorMessage = if (result.books.isEmpty()) "검색 결과가 없습니다." else null
                )
            } catch (e: java.io.IOException) {
                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    errorMessage = "네트워크 연결을 확인해주세요."
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    errorMessage = "검색 중 오류가 발생했습니다."
                )
            }
        }
    }

    fun loadNextPage() {
        val current = _searchState.value
        if (current.isLoadingMore || !current.hasMore || current.query.isBlank()) return

        viewModelScope.launch {
            val nextPage = current.currentPage + 1
            _searchState.value = current.copy(isLoadingMore = true)
            try {
                val result = searchBookWithTitleUseCase(current.query, nextPage)
                val hasMore = nextPage * PAGE_SIZE < result.totalBookCount
                _searchState.value = _searchState.value.copy(
                    books = _searchState.value.books + result.books,
                    currentPage = nextPage,
                    totalBookCount = result.totalBookCount,
                    isLoadingMore = false,
                    hasMore = hasMore
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(isLoadingMore = false)
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
                source = "ALADIN",
                aladinId = book.itemId.toInt(),
                title = book.title,
                author = book.author,
                publisher = book.publisher.ifBlank { null },
                pubDate = book.pubDate.ifBlank { null },
                isbn = book.isbn.ifBlank { null },
                pageCount = book.subInfo?.itemPage?.toIntOrNull(),
                description = book.description.ifBlank { null },
                coverImage = book.cover.ifBlank { null },
                reason = reason,
                startDate = startDate?.atStartOfDay(java.time.ZoneOffset.UTC)?.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")),
                endDate = endDate?.atStartOfDay(java.time.ZoneOffset.UTC)?.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
            )

            saveManualBookInfoUseCase(manual).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        _saveEvent.emit(SaveEvent.Success)
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
