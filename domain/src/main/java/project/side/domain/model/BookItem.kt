package project.side.domain.model

data class BookSearchResult(
    val totalBookCount: Int = 0,
    val books: List<BookItem> = emptyList()
)

data class BookItem(
    val title: String = "",
    val author: String = "",
    val cover: String = "",
    val isbn: String = "",
    val itemId: Long = 0L,
    val link: String = "",
    val publisher: String = "",
    val subInfo: BookSubInfo? = null
)

data class BookSubInfo(
    val itemPage: String? = null
)
