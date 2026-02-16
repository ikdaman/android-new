package project.side.remote.model.mybook

import project.side.data.model.StoreBookEntity
import project.side.data.model.StoreBookItemEntity

data class StoreBookResponse(
    val books: List<StoreBookItemResponse>,
    val totalPages: Int,
    val nowPage: Int,
    val totalElements: Int
) {
    fun toData(): StoreBookEntity = StoreBookEntity(
        content = books.map { it.toData() },
        totalPages = totalPages,
        totalElements = totalElements,
        last = nowPage >= totalPages - 1,
        first = nowPage == 0,
        size = books.size,
        number = nowPage,
        numberOfElements = books.size,
        empty = books.isEmpty()
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
