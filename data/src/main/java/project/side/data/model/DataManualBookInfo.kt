package project.side.data.model

import com.google.gson.annotations.SerializedName
import project.side.domain.model.ManualBookInfo

data class DataManualBookInfo(
    val title: String = "",
    val author: String = "",
    val publisher: String? = null,
    val pubDate: String? = null,
    val isbn: String? = null,
    val pageCount: String? = null,
    // JSON keys required by backend
    @SerializedName("startedDate") val startedDate: String? = null,
    @SerializedName("finishedDate") val finishedDate: String? = null
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
                startedDate = manualBookInfo.startDate,
                finishedDate = manualBookInfo.endDate
            )
        }
    }
}
