package project.side.remote.datasource

import project.side.data.datasource.HistoryDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.HistoryBookEntity
import project.side.remote.api.HistoryService
import javax.inject.Inject

class HistoryDataSourceImpl @Inject constructor(
    private val historyService: HistoryService
) : HistoryDataSource {
    override suspend fun getHistoryBooks(
        keyword: String?,
        page: Int?,
        size: Int?
    ): DataApiResult<HistoryBookEntity> {
        return try {
            val response = historyService.getHistoryBooks(keyword, page, size)
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("히스토리 목록을 불러올 수 없습니다.")
            } else {
                DataApiResult.Error("오류 발생: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류 : ${e.message}")
        }
    }
}