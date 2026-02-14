package project.side.domain.usecase.mybook

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.StoreBook
import project.side.domain.repository.MyBookRepository
import javax.inject.Inject

class GetStoreBooksUseCase @Inject constructor(
    private val myBookRepository: MyBookRepository
) {
    operator fun invoke(keyword: String? = null, page: Int? = null, size: Int? = null): Flow<DataResource<StoreBook>> =
        myBookRepository.getStoreBooks(keyword, page, size)
}
