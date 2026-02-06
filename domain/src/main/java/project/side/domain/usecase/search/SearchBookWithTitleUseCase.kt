package project.side.domain.usecase.search

import project.side.domain.repository.AladinRepository
import javax.inject.Inject

class SearchBookWithTitleUseCase @Inject constructor(
    private val repository: AladinRepository
) {
    suspend operator fun invoke(keyword: String, startPage: Int = 1) =
        repository.searchBookWithTitle(keyword, startPage)
}