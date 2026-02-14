package project.side.domain.usecase.mybook

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.MyBookSearch
import project.side.domain.repository.MyBookRepository
import javax.inject.Inject

class SearchMyBooksUseCase @Inject constructor(
    private val myBookRepository: MyBookRepository
) {
    operator fun invoke(query: String, page: Int? = null, size: Int? = null): Flow<DataResource<MyBookSearch>> =
        myBookRepository.searchMyBooks(query, page, size)
}
