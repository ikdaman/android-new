package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook

interface HistoryRepository {
    suspend fun getHistoryBooks(page: Int, limit: Int, sort: String): Flow<DataResource<HistoryBook>>
}