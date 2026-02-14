package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getStoreBooksUseCase: GetStoreBooksUseCase
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

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents = _snackbarEvents.asSharedFlow()

    private var storeBooksPage = 0
    private var storeBooksLastPage = false
    private var storeBooksLoading = false

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

    private fun fetchStoreBooks() {
        if (storeBooksLoading || storeBooksLastPage) return
        storeBooksLoading = true
        viewModelScope.launch {
            getStoreBooksUseCase(page = storeBooksPage, size = PAGE_SIZE).collect { result ->
                if (result is DataResource.Success) {
                    _storeBooks.value = _storeBooks.value + result.data.content
                    storeBooksLastPage = result.data.last
                    storeBooksPage++
                }
                storeBooksLoading = false
            }
        }
    }

    fun loadMore() {
        fetchStoreBooks()
    }

    fun refreshStoreBooks() {
        storeBooksPage = 0
        storeBooksLastPage = false
        storeBooksLoading = false
        _storeBooks.value = emptyList()
        fetchStoreBooks()
    }

    suspend fun showSnackbar(message: String) {
        _snackbarEvents.emit(message)
    }
}
