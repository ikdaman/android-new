package project.side.domain.model

data class ManualBookInfo(
    val title: String = "",
    val author: String = "",
    val publisher: String? = null,
    val pubDate: String? = null,
    val isbn: String? = null,
    val pageCount: String? = null
)
