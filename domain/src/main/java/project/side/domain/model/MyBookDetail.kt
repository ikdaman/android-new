package project.side.domain.model

data class MyBookDetail(
    val mybookId: String,
    val readingStatus: String,
    val shelfType: String,
    val createdDate: String,
    val reason: String?,
    val bookInfo: MyBookDetailBookInfo,
    val historyInfo: MyBookDetailHistoryInfo
)

data class MyBookDetailBookInfo(
    val bookId: String,
    val source: String,
    val title: String,
    val author: String,
    val coverImage: String?,
    val publisher: String?,
    val totalPage: Int?,
    val publishDate: String?,
    val isbn: String?,
    val aladinId: String?
)

data class MyBookDetailHistoryInfo(
    val startedDate: String?,
    val finishedDate: String?
)
