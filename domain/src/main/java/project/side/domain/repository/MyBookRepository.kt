package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo
import project.side.domain.model.MyBookDetail
import project.side.domain.model.MyBookSearch
import project.side.domain.model.StoreBook

interface MyBookRepository {
    fun getMyBookDetail(mybookId: Int): Flow<DataResource<MyBookDetail>>
    fun deleteMyBook(mybookId: Int): Flow<DataResource<Unit>>
    fun searchMyBooks(query: String, page: Int?, size: Int?): Flow<DataResource<MyBookSearch>>
    fun getStoreBooks(keyword: String?, page: Int?, size: Int?): Flow<DataResource<StoreBook>>
    fun updateReadingStatus(mybookId: Int, startedDate: String?, finishedDate: String?): Flow<DataResource<Int>>
    fun updateMyBook(
        mybookId: Int,
        status: String? = null,
        reason: String? = null,
        startedDate: String? = null,
        finishedDate: String? = null,
        bookInfoTitle: String? = null,
        bookInfoAuthor: String? = null,
        bookInfoPublisher: String? = null,
        bookInfoPublishDate: String? = null,
        bookInfoIsbn: String? = null,
        bookInfoTotalPage: Int? = null
    ): Flow<DataResource<Int>>
    fun saveMyBook(info: ManualBookInfo): Flow<DataResource<Int>>
}
