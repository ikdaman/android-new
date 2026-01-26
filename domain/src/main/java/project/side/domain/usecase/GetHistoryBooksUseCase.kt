package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook
import project.side.domain.repository.HistoryRepository
import javax.inject.Inject

class GetHistoryBooksUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(
        page: Int,
        limit: Int,
        sort: String
    ): Flow<DataResource<HistoryBook>> =
        historyRepository.getHistoryBooks(page, limit, sort)
}