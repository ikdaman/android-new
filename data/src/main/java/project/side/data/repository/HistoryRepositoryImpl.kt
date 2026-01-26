package project.side.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import project.side.data.datasource.HistoryDataSource
import project.side.data.model.DataApiResult
import project.side.domain.DataResource
import project.side.domain.model.HistoryBook
import project.side.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource
) : HistoryRepository {
    override suspend fun getHistoryBooks(
        page: Int,
        limit: Int,
        sort: String
    ): Flow<DataResource<HistoryBook>> = flow {
        emit(DataResource.Loading())
        val result = historyDataSource.getHistoryBooks(page, limit, sort)
        if (result is DataApiResult.Success) {
            emit(DataResource.Success(result.data.toDomain()))
        } else if (result is DataApiResult.Error) {
            emit(DataResource.Error(result.message))
        }
    }.catch {
        emit(DataResource.Error(it.message ?: "네트워크 오류"))
    }
}