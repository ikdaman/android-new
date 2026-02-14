package project.side.data.model

import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookDetailBookInfo
import project.side.domain.model.MyBookDetailHistoryInfo

data class MyBookDetailEntity(
    val mybookId: String,
    val readingStatus: String,
    val shelfType: String,
    val createdDate: String,
    val reason: String?,
    val bookInfo: BookInfoEntity,
    val historyInfo: HistoryInfoEntity
) {
    fun toDomain(): MyBookDetail = MyBookDetail(
        mybookId = mybookId,
        readingStatus = readingStatus,
        shelfType = shelfType,
        createdDate = createdDate,
        reason = reason,
        bookInfo = bookInfo.toDomain(),
        historyInfo = historyInfo.toDomain()
    )
}

data class BookInfoEntity(
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
) {
    fun toDomain(): MyBookDetailBookInfo = MyBookDetailBookInfo(
        bookId = bookId,
        source = source,
        title = title,
        author = author,
        coverImage = coverImage,
        publisher = publisher,
        totalPage = totalPage,
        publishDate = publishDate,
        isbn = isbn,
        aladinId = aladinId
    )
}

data class HistoryInfoEntity(
    val startedDate: String?,
    val finishedDate: String?
) {
    fun toDomain(): MyBookDetailHistoryInfo = MyBookDetailHistoryInfo(
        startedDate = startedDate,
        finishedDate = finishedDate
    )
}
