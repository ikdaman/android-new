package project.side.domain.model

data class ManualBookInfo(
    val source: String = "CUSTOM",
    val aladinId: Int? = null,
    val title: String = "",
    val author: String = "",
    val publisher: String? = null,
    val pubDate: String? = null,
    val isbn: String? = null,
    val pageCount: Int? = null,
    val description: String? = null,
    val coverImage: String? = null,
    val reason: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)
