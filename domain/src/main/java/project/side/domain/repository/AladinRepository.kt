package project.side.domain.repository

import project.side.domain.model.BookSearchResult


interface AladinRepository {
    suspend fun searchBookWithTitle(
        title: String,
        startPage: Int
    ): BookSearchResult

    suspend fun searchBookWithIsbn(isbn: String): BookSearchResult
}