package project.side.remote.model.mybook

import com.squareup.moshi.Json

data class MyBookUpdateRequest(
    val status: String?,
    val reason: String?,
    val historyInfo: HistoryInfoRequest?,
    val bookInfo: BookInfoRequest?
)

data class HistoryInfoRequest(
    val startedDate: String?,
    val finishedDate: String?
)

data class BookInfoRequest(
    val title: String?,
    val author: String?,
    val publisher: String?,
    val publishDate: String?,
    @Json(name = "ISBN") val isbn: String?,
    val totalPage: Int?
)
