package project.side.remote.datasource

import android.accounts.NetworkErrorException
import project.side.data.datasource.BackendDataSource
import project.side.data.model.SaveResultEntity
import project.side.remote.api.BackendApiService
import project.side.data.model.DataManualBookInfo

import javax.inject.Inject

class BackendDataSourceImpl @Inject constructor(
    private val backendApiService: BackendApiService
): BackendDataSource {
    override suspend fun saveManualBookInfo(dataManualBookInfo: DataManualBookInfo): SaveResultEntity {
        val response = backendApiService.saveManualBookInfo(dataManualBookInfo)
        if (response.isSuccessful) {
            val body = response.body() ?: throw NetworkErrorException("Response body is null")
            return SaveResultEntity(code = body.code, message = body.message)
        } else {
            throw NetworkErrorException("Response is not Successful")
        }
    }
}
