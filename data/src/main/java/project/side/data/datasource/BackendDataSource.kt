package project.side.data.datasource

import project.side.data.model.DataManualBookInfo
import project.side.data.model.SaveResultEntity

interface BackendDataSource {
    suspend fun saveManualBookInfo(dataManualBookInfo: DataManualBookInfo): SaveResultEntity
}
