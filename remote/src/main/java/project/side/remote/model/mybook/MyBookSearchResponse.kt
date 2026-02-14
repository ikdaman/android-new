package project.side.remote.model.mybook

import project.side.data.model.MyBookSearchEntity
import project.side.data.model.MyBookSearchItemEntity

data class MyBookSearchResponse(
    val totalPages: Int,
    val nowPage: Int,
    val totalElements: Int,
    val books: List<MyBookSearchItemResponse>
) {
    fun toData(): MyBookSearchEntity = MyBookSearchEntity(
        totalPages = totalPages,
        nowPage = nowPage,
        totalElements = totalElements,
        books = books.map { it.toData() }
    )
}

data class MyBookSearchItemResponse(
    val mybookId: Int,
    val readingStatus: String,
    val createdDate: String,
    val startedDate: String?,
    val finishedDate: String?,
    val bookInfo: SearchBookInfoResponse
) {
    fun toData(): MyBookSearchItemEntity = MyBookSearchItemEntity(
        mybookId = mybookId,
        readingStatus = readingStatus,
        createdDate = createdDate,
        startedDate = startedDate,
        finishedDate = finishedDate,
        title = bookInfo.title,
        author = bookInfo.author,
        coverImage = bookInfo.coverImage,
        description = bookInfo.description
    )
}

data class SearchBookInfoResponse(
    val title: String,
    val author: List<String>,
    val coverImage: String?,
    val description: String?
)
