package project.side.data.datasource

import project.side.data.model.BookSearchResponse

interface AladinBookSearchSource {
    suspend fun searchBookWithTitle(query: String, startPage: Int): BookSearchResponse
    suspend fun searchBookWithIsbn(itemId: String): BookSearchResponse
}