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
                coverImage = it.bookInfo.coverImage,
                startedDate = it.historyInfo.startedDate,
                finishedDate = it.historyInfo.finishedDate
            )
        }
    )
}

data class HistoryBook(
    val mybookId: Int,
    val bookInfo: HistoryBookInfo,
    val historyInfo: HistoryInfo
)

data class HistoryBookInfo(
    val title: String,
    val coverImage: String
)

data class HistoryInfo(
    val startedDate: String,
    val finishedDate: String?
)


