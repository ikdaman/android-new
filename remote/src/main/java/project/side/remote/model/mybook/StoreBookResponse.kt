package project.side.remote.model.mybook

import project.side.data.model.StoreBookEntity
import project.side.data.model.StoreBookItemEntity

data class StoreBookResponse(
    val content: List<StoreBookItemResponse>,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val empty: Boolean
) {
    fun toData(): StoreBookEntity = StoreBookEntity(
        content = content.map { it.toData() },
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

data class StoreBookItemResponse(
    val mybookId: Int,
    val createdDate: String,
    val bookInfo: SearchBookInfoResponse
) {
    fun toData(): StoreBookItemEntity = StoreBookItemEntity(
        mybookId = mybookId,
        createdDate = createdDate,
        title = bookInfo.title,
        author = bookInfo.author,
        coverImage = bookInfo.coverImage,
        description = bookInfo.description
    )
}
