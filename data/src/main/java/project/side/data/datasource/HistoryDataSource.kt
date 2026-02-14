package project.side.data.datasource

import project.side.data.model.DataApiResult
import project.side.data.model.HistoryBookEntity

interface HistoryDataSource {
    suspend fun getHistoryBooks(keyword: String?, page: Int?, size: Int?): DataApiResult<HistoryBookEntity>
}