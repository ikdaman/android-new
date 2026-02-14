package project.side.data.repository

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import project.side.data.datasource.MyBookDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.MyBookUpdateEntity
import project.side.domain.DataResource
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookSearch
import project.side.domain.model.StoreBook
import project.side.domain.repository.MyBookRepository
import javax.inject.Inject

class MyBookRepositoryImpl @Inject constructor(
    private val myBookDataSource: MyBookDataSource
) : MyBookRepository {

    override fun getMyBookDetail(mybookId: Int) = flow<DataResource<MyBookDetail>> {
        emit(DataResource.Loading())
        when (val result = myBookDataSource.getMyBookDetail(mybookId)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data.toDomain()))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun deleteMyBook(mybookId: Int) = flow<DataResource<Unit>> {
        emit(DataResource.Loading())
        when (val result = myBookDataSource.deleteMyBook(mybookId)) {
            is DataApiResult.Success -> emit(DataResource.Success(Unit))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun searchMyBooks(query: String, page: Int?, size: Int?) = flow<DataResource<MyBookSearch>> {
        emit(DataResource.Loading())
        when (val result = myBookDataSource.searchMyBooks(query, page, size)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data.toDomain()))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun getStoreBooks(keyword: String?, page: Int?, size: Int?) = flow<DataResource<StoreBook>> {
        emit(DataResource.Loading())
        when (val result = myBookDataSource.getStoreBooks(keyword, page, size)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data.toDomain()))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun updateReadingStatus(mybookId: Int, startedDate: String?, finishedDate: String?) = flow<DataResource<Int>> {
        emit(DataResource.Loading())
        when (val result = myBookDataSource.updateReadingStatus(mybookId, startedDate, finishedDate)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun updateMyBook(mybookId: Int, reason: String?, startedDate: String?, finishedDate: String?) = flow<DataResource<Int>> {
        emit(DataResource.Loading())
        val entity = MyBookUpdateEntity(reason = reason, startedDate = startedDate, finishedDate = finishedDate)
        when (val result = myBookDataSource.updateMyBook(mybookId, entity)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }
}
