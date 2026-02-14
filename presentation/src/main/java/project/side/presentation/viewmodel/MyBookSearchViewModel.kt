package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.side.domain.DataResource
import project.side.domain.model.MyBookSearchItem
import project.side.domain.usecase.mybook.SearchMyBooksUseCase
import javax.inject.Inject

private const val PAGE_SIZE = 10

@HiltViewModel
class MyBookSearchViewModel @Inject constructor(
    private val searchMyBooksUseCase: SearchMyBooksUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MyBookSearchItem>>(emptyList())
    val searchResults: StateFlow<List<MyBookSearchItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 0
    private var isLastPage = false
    private var isLoadingMore = false

    fun search(query: String) {
        if (query.isBlank()) return
        _query.value = query
        currentPage = 0
        isLastPage = false
        _searchResults.value = emptyList()
        loadPage(query, 0, isNewSearch = true)
    }

    fun loadMore() {
        if (isLastPage || isLoadingMore || _query.value.isBlank()) return
        loadPage(_query.value, currentPage + 1, isNewSearch = false)
    }

    private fun loadPage(query: String, page: Int, isNewSearch: Boolean) {
        viewModelScope.launch {
            if (isNewSearch) _isLoading.value = true
            isLoadingMore = true

            searchMyBooksUseCase(query, page, PAGE_SIZE).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        val data = result.data
                        currentPage = page
                        isLastPage = data.books.size < PAGE_SIZE
                        _searchResults.value = if (isNewSearch) {
                            data.books
                        } else {
                            _searchResults.value + data.books
                        }
                        _isLoading.value = false
                        isLoadingMore = false
                    }
                    is DataResource.Error -> {
                        _isLoading.value = false
                        isLoadingMore = false
                    }
                    is DataResource.Loading -> { /* no-op */ }
                }
            }
        }
    }
}
