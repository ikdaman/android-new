package project.side.data.model

data class MyBookUpdateEntity(
    val status: String? = null,
    val reason: String? = null,
    val startedDate: String? = null,
    val finishedDate: String? = null,
    val bookInfo: BookInfoUpdateEntity? = null
)

data class BookInfoUpdateEntity(
    val title: String? = null,
    val author: String? = null,
    val publisher: String? = null,
    val publishDate: String? = null,
    val isbn: String? = null,
    val totalPage: Int? = null
)
