package project.side.data.model

import project.side.domain.model.ManualBookInfo

data class SaveMyBookEntity(
    val source: String,
    val aladinId: Int?,
    val isbn: String?,
    val title: String,
    val author: String,
    val publisher: String?,
    val description: String?,
    val totalPage: Int?,
    val publishDate: String?,
    val coverImage: String?,
    val reason: String?,
    val startedDate: String?,
    val finishedDate: String?
) {
    companion object {
        fun fromDomain(info: ManualBookInfo): SaveMyBookEntity {
            return SaveMyBookEntity(
                source = info.source,
                aladinId = info.aladinId,
                isbn = info.isbn,
                title = info.title,
                author = info.author,
                publisher = info.publisher,
                description = info.description,
                totalPage = info.pageCount,
                publishDate = info.pubDate,
                coverImage = info.coverImage,
                reason = info.reason,
                startedDate = info.startDate,
                finishedDate = info.endDate
            )
        }
    }
}
