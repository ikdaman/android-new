package project.side.domain.repository

import project.side.domain.DataResource
import kotlinx.coroutines.flow.Flow
import project.side.domain.model.ManualBookInfo

interface BackendRepository {
    // emit only whether save succeeded (true) or failed (false) wrapped in DataResource
    fun saveManualBookInfo(manualBookInfo: ManualBookInfo): Flow<DataResource<Boolean>>
}
