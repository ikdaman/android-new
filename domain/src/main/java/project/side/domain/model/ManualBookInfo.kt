package project.side.domain.model

data class ManualBookInfo(
    val title: String = "",
    val author: String = "",
    val publisher: String? = null,
    val pubDate: String? = null,
    val isbn: String? = null,
    val pageCount: String? = null
    ,
    val reason: String? = null,
    val startDate: String? = null, // ISO yyyy-MM-dd
    val endDate: String? = null // ISO yyyy-MM-dd
)
