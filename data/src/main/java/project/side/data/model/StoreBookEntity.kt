package project.side.data.model

import project.side.domain.model.StoreBook
import project.side.domain.model.StoreBookItem

data class StoreBookEntity(
    val content: List<StoreBookItemEntity>,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val empty: Boolean
) {
    fun toDomain(): StoreBook = StoreBook(
        content = content.map { it.toDomain() },
        totalPages = totalPages,
        totalElements = totalElements,
        last = last,
        first = first,
        size = size,
        number = number,
        numberOfElements = numberOfElements,
        empty = empty
    )
}

data class StoreBookItemEntity(
    val mybookId: Int,
    val createdDate: String,
    val title: String,
    val author: List<String>,
    val coverImage: String?,
    val description: String?
) {
    fun toDomain(): StoreBookItem = StoreBookItem(
        mybookId = mybookId,
        createdDate = createdDate,
        title = title,
        author = author,
        coverImage = coverImage,
        description = description
    )
}
