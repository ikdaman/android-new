package project.side.domain.usecase.mybook

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.repository.MyBookRepository
import javax.inject.Inject

class DeleteMyBookUseCase @Inject constructor(
    private val myBookRepository: MyBookRepository
) {
    operator fun invoke(mybookId: Int): Flow<DataResource<Unit>> =
        myBookRepository.deleteMyBook(mybookId)
}
