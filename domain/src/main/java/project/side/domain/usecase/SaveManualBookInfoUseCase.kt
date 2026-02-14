package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo
import project.side.domain.repository.MyBookRepository
import javax.inject.Inject

class SaveManualBookInfoUseCase @Inject constructor(
    private val myBookRepository: MyBookRepository
) {
    operator fun invoke(manualBookInfo: ManualBookInfo): Flow<DataResource<Int>> {
        return myBookRepository.saveMyBook(manualBookInfo)
    }
}
