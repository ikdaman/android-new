package project.side.domain.model

data class StoreBook(
    val content: List<StoreBookItem>,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val empty: Boolean
)

data class StoreBookItem(
    val mybookId: Int,
    val createdDate: String,
    val title: String,
    val author: List<String>,
    val coverImage: String?,
    val description: String?
)
