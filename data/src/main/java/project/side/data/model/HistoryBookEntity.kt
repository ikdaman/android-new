package project.side.data.model

import project.side.domain.model.HistoryBook
import project.side.domain.model.HistoryBookInfo

data class HistoryBookEntity(
    val totalPages: Int,
    val nowPage: Int,
    val books: List<HistoryBookInfoEntity>
) {
    fun toDomain(): HistoryBook = HistoryBook(
        totalPages = totalPages,
        nowPage = nowPage,
        books = books.map {
            HistoryBookInfo(
                mybookId = it.mybookId,
                title = it.title,
                coverImage = it.coverImage,
                startedDate = it.startedDate,
                finishedDate = it.finishedDate
            )
        }
    )
}

data class HistoryBookInfoEntity(
    val mybookId: Int,
    val title: String,
    val coverImage: String,
    val startedDate: String,
    val finishedDate: String?
)
