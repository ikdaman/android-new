package project.side.data.repository

import project.side.data.datasource.AladinBookSearchSource
import project.side.data.model.toDomain
import project.side.domain.model.BookSearchResult
import project.side.domain.repository.AladinRepository
import javax.inject.Inject

class AladinRepositoryImpl @Inject constructor(private val bookSearchSource: AladinBookSearchSource) :
    AladinRepository {
    override suspend fun searchBookWithTitle(
        title: String,
        startPage: Int
    ): BookSearchResult {
        val response = bookSearchSource.searchBookWithTitle(
            query = title,
            startPage = startPage
        )
        return response.toDomain()
    }

    override suspend fun searchBookWithIsbn(isbn: String): BookSearchResult {
        val response = bookSearchSource.searchBookWithIsbn(itemId = isbn)
        return response.toDomain()
    }
}