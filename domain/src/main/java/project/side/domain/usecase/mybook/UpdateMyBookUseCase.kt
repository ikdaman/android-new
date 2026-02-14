package project.side.domain.usecase.mybook

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.repository.MyBookRepository
import javax.inject.Inject

class UpdateMyBookUseCase @Inject constructor(
    private val myBookRepository: MyBookRepository
) {
    operator fun invoke(mybookId: Int, reason: String? = null, startedDate: String? = null, finishedDate: String? = null): Flow<DataResource<Int>> =
        myBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
}
