package project.side.data.repository

import project.side.data.datasource.AladinBookSearchSource
import project.side.data.model.toDomain
import project.side.domain.model.BookSearchResult
import project.side.domain.repository.AladinRepository
import javax.inject.Inject

val TAG = "BookRepositoryImpl"

class AladinRepositoryImpl @Inject constructor(private val bookSearchSource: AladinBookSearchSource) :
    AladinRepository {
    override suspend fun searchBookWithTitle(
        title: String,
        startPage: Int
    ): BookSearchResult {
        try {
            val response = bookSearchSource.searchBookWithTitle(
                query = title,
                startPage = startPage
            )
            println("searchBookWithTitle: $response")
            return response.toDomain()
        } catch (e: Exception) {
            println("searchBookWithTitle: $e")
            return BookSearchResult()
        }
    }

    override suspend fun searchBookWithIsbn(isbn: String): BookSearchResult {
        try {
            val response = bookSearchSource.searchBookWithIsbn(itemId = isbn)
            return response.toDomain()
        } catch (e: Exception) {
            println("searchBookWithIsbn: $e")
            return BookSearchResult()
        }
    }
}