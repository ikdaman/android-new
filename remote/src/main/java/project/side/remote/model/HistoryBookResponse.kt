package project.side.remote.model

import project.side.data.model.HistoryBookEntity
import project.side.data.model.HistoryBookInfoEntity

data class HistoryBookResponse(
    val totalPages: Int,
    val nowPage: Int,
    val books: List<HistoryBook>
) {
    fun toData() = HistoryBookEntity(
        totalPages = totalPages,
        nowPage = nowPage,
        books = books.map {
            HistoryBookInfoEntity(
                mybookId = it.mybookId,
                title = it.bookInfo.title,
                author = it.bookInfo.author,
                coverImage = it.bookInfo.coverImage,
                description = it.bookInfo.description,
                startedDate = it.startedDate,
                finishedDate = it.finishedDate
            )
        }
    )
}

data class HistoryBook(
    val mybookId: Int,
    val startedDate: String,
    val finishedDate: String?,
    val bookInfo: HistoryBookInfo
)

data class HistoryBookInfo(
    val title: String,
    val author: List<String>?,
    val coverImage: String,
    val description: String?
)


