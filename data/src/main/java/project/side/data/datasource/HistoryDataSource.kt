package project.side.data.datasource

import project.side.data.model.DataApiResult
import project.side.data.model.HistoryBookEntity

interface HistoryDataSource {
    suspend fun getHistoryBooks(
        page: Int,
        limit: Int,
        sort: String
    ): DataApiResult<HistoryBookEntity>
}