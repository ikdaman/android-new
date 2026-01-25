package project.side.remote.datasource

import project.side.data.datasource.AladinBookSearchSource
import project.side.data.model.BookSearchResponse
import project.side.remote.api.AladinBookService
import javax.inject.Inject

class AladinBookSearchSourceImpl @Inject constructor(
    private val service: AladinBookService
): AladinBookSearchSource {
    override suspend fun searchBookWithTitle(
        query: String,
        startPage: Int
    ): BookSearchResponse {
        return service.searchBookWithTitle(query = query, startPage = startPage)
    }

    override suspend fun searchBookWithIsbn(itemId: String): BookSearchResponse {
        return service.searchBookWithIsbn(itemId = itemId)
    }

}