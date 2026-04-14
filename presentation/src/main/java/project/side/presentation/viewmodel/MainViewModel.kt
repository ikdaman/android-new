package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
import project.side.domain.model.StoreBookItem
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import project.side.domain.usecase.mybook.GetStoreBooksUseCase
import project.side.domain.usecase.mybook.DeleteMyBookUseCase
import project.side.domain.usecase.mybook.UpdateReadingStatusUseCase
import project.side.presentation.util.SnackbarManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getStoreBooksUseCase: GetStoreBooksUseCase,
    private val updateReadingStatusUseCase: UpdateReadingStatusUseCase,
    private val deleteMyBookUseCase: DeleteMyBookUseCase
): ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = getLoginStateUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _storeBooks = MutableStateFlow<List<StoreBookItem>>(emptyList())
    val storeBooks: StateFlow<List<StoreBookItem>> = _storeBooks.asStateFlow()

    private val _storeBooksError = MutableStateFlow<String?>(null)
    val storeBooksError: StateFlow<String?> = _storeBooksError.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents = _snackbarEvents.asSharedFlow()

    private val _sortDescending = MutableStateFlow(true)
    val sortDescending: StateFlow<Boolean> = _sortDescending.asStateFlow()

    private var storeBooksPage = 0
    private var storeBooksLastPage = false
    private var storeBooksLoading = false
    private var fetchJob: Job? = null

    companion object {
        private const val PAGE_SIZE = 5
    }

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

    private fun fetchStoreBooks(isRefresh: Boolean = false) {
        if (storeBooksLoading || storeBooksLastPage) return
        storeBooksLoading = true
        fetchJob = viewModelScope.launch {
            val sort = if (_sortDescending.value) "createdAt,desc" else "createdAt,asc"
            getStoreBooksUseCase(page = storeBooksPage, size = PAGE_SIZE, sort = sort).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        _storeBooks.value = if (isRefresh) result.data.content else _storeBooks.value + result.data.content
                        storeBooksLastPage = result.data.last
                        storeBooksPage++
                        storeBooksLoading = false
                        _storeBooksError.value = null
                    }
                    is DataResource.Error -> {
                        storeBooksLoading = false
                        _storeBooksError.value = result.message ?: "책 목록을 불러오지 못했어요."
                    }
                    is DataResource.Loading -> {}
                }
            }
        }
    }

    fun loadMore() {
        fetchStoreBooks()
    }

    fun toggleSort() {
        _sortDescending.value = !_sortDescending.value
        refreshStoreBooks()
    }

    fun refreshStoreBooks() {
        fetchJob?.cancel()
        storeBooksPage = 0
        storeBooksLastPage = false
        storeBooksLoading = false
        fetchStoreBooks(isRefresh = true)
    }

    suspend fun showSnackbar(message: String) {
        _snackbarEvents.emit(message)
    }

    fun deleteBook(mybookId: Int) {
        viewModelScope.launch {
            deleteMyBookUseCase(mybookId).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        refreshStoreBooks()
                        SnackbarManager.show("책이 삭제되었어요.")
                    }
                    is DataResource.Error -> {
                        SnackbarManager.show(result.message ?: "삭제에 실패했어요")
                    }
                    is DataResource.Loading -> {}
                }
            }
        }
    }

    fun startReading(mybookId: Int) {
        val today = LocalDate.now().atStartOfDay(java.time.ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
        viewModelScope.launch {
            updateReadingStatusUseCase(mybookId, startedDate = today).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        SnackbarManager.show("시작한 책은 히스토리에서 볼 수 있어요.")
                    }
                    is DataResource.Error -> {
                        SnackbarManager.show(result.message ?: "독서 시작에 실패했어요")
                    }
                    is DataResource.Loading -> {}
                }
            }
        }
    }
}
