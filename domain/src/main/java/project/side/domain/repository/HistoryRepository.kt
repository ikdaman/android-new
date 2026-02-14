package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook

interface HistoryRepository {
    suspend fun getHistoryBooks(keyword: String?, page: Int?, size: Int?): Flow<DataResource<HistoryBook>>
}