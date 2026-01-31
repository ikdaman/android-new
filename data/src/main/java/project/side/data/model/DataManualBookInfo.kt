package project.side.data.model

import project.side.domain.model.ManualBookInfo

data class DataManualBookInfo(
    val title: String = "",
    val author: String = "",
    val publisher: String? = null,
    val pubDate: String? = null,
    val isbn: String? = null,
    val pageCount: String? = null
    ,
    val startDate: String? = null,
    val endDate: String? = null
) {
    companion object {
        fun fromDomain(manualBookInfo: ManualBookInfo): DataManualBookInfo {
            return DataManualBookInfo(
                title = manualBookInfo.title,
                author = manualBookInfo.author,
                publisher = manualBookInfo.publisher,
                pubDate = manualBookInfo.pubDate,
                isbn = manualBookInfo.isbn,
                pageCount = manualBookInfo.pageCount,
                startDate = manualBookInfo.startDate,
                endDate = manualBookInfo.endDate
            )
        }
    }
}
