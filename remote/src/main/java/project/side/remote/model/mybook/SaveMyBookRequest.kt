package project.side.remote.model.mybook

data class SaveMyBookRequest(
    val bookInfo: SaveBookInfoRequest,
    val historyInfo: SaveHistoryInfoRequest?,
    val reason: String?
)

data class SaveBookInfoRequest(
    val source: String,
    val aladinId: Int?,
    val isbn: String?,
    val title: String,
    val author: String,
    val publisher: String?,
    val description: String?,
    val totalPage: Int?,
    val publishDate: String?,
    val coverImage: String?
)

data class SaveHistoryInfoRequest(
    val startedDate: String?,
    val finishedDate: String?
)
