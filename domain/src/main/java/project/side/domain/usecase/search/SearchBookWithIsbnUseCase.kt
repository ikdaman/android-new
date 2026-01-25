package project.side.domain.usecase.search

import project.side.domain.repository.AladinRepository
import javax.inject.Inject

class SearchBookWithIsbnUseCase @Inject constructor(
    private val repository: AladinRepository
) {
    suspend operator fun invoke(isbn: String) = repository.searchBookWithIsbn(isbn)
}