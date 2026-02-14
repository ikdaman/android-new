package project.side.data.model

import project.side.domain.model.MyBookSearch
import project.side.domain.model.MyBookSearchItem

data class MyBookSearchEntity(
    val totalPages: Int,
    val nowPage: Int,
    val totalElements: Int,
    val books: List<MyBookSearchItemEntity>
) {
    fun toDomain(): MyBookSearch = MyBookSearch(
        totalPages = totalPages,
        nowPage = nowPage,
        totalElements = totalElements,
        books = books.map { it.toDomain() }
    )
}

data class MyBookSearchItemEntity(
    val mybookId: Int,
    val readingStatus: String,
    val createdDate: String,
    val startedDate: String?,
    val finishedDate: String?,
    val title: String,
    val author: List<String>,
    val coverImage: String?,
    val description: String?
) {
    fun toDomain(): MyBookSearchItem = MyBookSearchItem(
        mybookId = mybookId,
        readingStatus = readingStatus,
        createdDate = createdDate,
        startedDate = startedDate,
        finishedDate = finishedDate,
        title = title,
        author = author,
        coverImage = coverImage,
        description = description
    )
}
