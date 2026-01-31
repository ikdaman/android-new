package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo
import project.side.domain.repository.BackendRepository
import javax.inject.Inject

class SaveManualBookInfoUseCase @Inject constructor(
    private val backendRepository: BackendRepository
) {
    // returns DataResource<Boolean> where true == saved (code 201)
    operator fun invoke(manualBookInfo: ManualBookInfo): Flow<DataResource<Boolean>> {
        return backendRepository.saveManualBookInfo(manualBookInfo)
    }
}
