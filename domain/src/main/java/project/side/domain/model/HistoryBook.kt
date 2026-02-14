package project.side.domain.model

data class HistoryBook(
    val totalPages: Int,
    val nowPage: Int,
    val books: List<HistoryBookInfo>
)

data class HistoryBookInfo (
    val mybookId: Int,
    val title: String,
    val author: List<String>?,
    val coverImage: String,
    val description: String?,
    val startedDate: String,
    val finishedDate: String?
)
