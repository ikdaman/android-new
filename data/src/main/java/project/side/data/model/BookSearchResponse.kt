package project.side.data.model

import project.side.domain.model.BookItem
import project.side.domain.model.BookSearchResult
import project.side.domain.model.BookSubInfo

data class BookSearchResponse(
    val totalResults: Int,
    val item: List<BookSearchItem>
)

data class BookSearchItem(
    val title: String,
    val link: String,
    val author: String,
    val cover: String,
    val publisher: String,
    val isbn: String?,
    val isbn13: String?,
    val itemId: Long,
    val subInfo: BookSubInfoResponse? = null
)

data class BookSubInfoResponse(
    val itemPage: String? = null
)

fun BookSearchResponse.toDomain() = BookSearchResult(
    totalBookCount = totalResults,
    books = item.map {
        BookItem(
            title = it.title,
            author = it.author,
            cover = it.cover,
            publisher = it.publisher,
            isbn = it.isbn13 ?: it.isbn ?: "",
            itemId = it.itemId,
            link = it.link,
            subInfo = it.subInfo.toDomain()
        )
    }
)

private fun BookSubInfoResponse?.toDomain() = if (this == null) null else BookSubInfo(itemPage = itemPage)