package project.side.domain.model

data class MyBookSearch(
    val totalPages: Int,
    val nowPage: Int,
    val totalElements: Int,
    val books: List<MyBookSearchItem>
)

data class MyBookSearchItem(
    val mybookId: Int,
    val readingStatus: String,
    val createdDate: String,
    val startedDate: String?,
    val finishedDate: String?,
    val title: String,
    val author: List<String>,
    val coverImage: String?,
    val description: String?
)
