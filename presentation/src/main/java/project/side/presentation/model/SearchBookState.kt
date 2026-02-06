package project.side.presentation.model

import project.side.domain.model.BookItem

data class SearchBookState(
    val query: String = "",
    val books: List<BookItem> = emptyList(),
    val currentPage: Int = 1,
    val totalBookCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val errorMessage: String? = null
)
