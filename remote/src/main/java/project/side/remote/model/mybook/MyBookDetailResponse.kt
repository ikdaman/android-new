package project.side.remote.model.mybook

import project.side.data.model.BookInfoEntity
import project.side.data.model.HistoryInfoEntity
import project.side.data.model.MyBookDetailEntity

data class MyBookDetailResponse(
    val mybookId: String,
    val readingStatus: String,
    val shelfType: String,
    val createdDate: String,
    val reason: String?,
    val bookInfo: BookInfoResponse,
    val historyInfo: HistoryInfoResponse
) {
    fun toData(): MyBookDetailEntity = MyBookDetailEntity(
        mybookId = mybookId,
        readingStatus = readingStatus,
        shelfType = shelfType,
        createdDate = createdDate,
        reason = reason,
        bookInfo = bookInfo.toData(),
        historyInfo = historyInfo.toData()
    )
}

data class BookInfoResponse(
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
    fun toData(): BookInfoEntity = BookInfoEntity(
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

data class HistoryInfoResponse(
    val startedDate: String?,
    val finishedDate: String?
) {
    fun toData(): HistoryInfoEntity = HistoryInfoEntity(
        startedDate = startedDate,
        finishedDate = finishedDate
    )
}
