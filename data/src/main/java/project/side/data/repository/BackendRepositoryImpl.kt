package project.side.data.repository

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import project.side.data.datasource.BackendDataSource
import project.side.data.model.DataManualBookInfo
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo
import project.side.domain.repository.BackendRepository
import javax.inject.Inject

class BackendRepositoryImpl @Inject constructor(
    private val backendDataSource: BackendDataSource
): BackendRepository {
    override fun saveManualBookInfo(manualBookInfo: ManualBookInfo) = flow {
        emit(DataResource.loading())
        val result = backendDataSource.saveManualBookInfo(DataManualBookInfo.fromDomain(manualBookInfo))
        // treat save success as code == 201
        val success = (result.code == 201)
        emit(DataResource.success(success))
    }.catch { e ->
        emit(DataResource.error(e.message))
    }
}
