package project.side.data.datasource

import project.side.data.model.DataApiResult
import project.side.data.model.MyBookDetailEntity
import project.side.data.model.MyBookSearchEntity
import project.side.data.model.MyBookUpdateEntity
import project.side.data.model.SaveMyBookEntity
import project.side.data.model.StoreBookEntity

interface MyBookDataSource {
    suspend fun getMyBookDetail(mybookId: Int): DataApiResult<MyBookDetailEntity>
    suspend fun deleteMyBook(mybookId: Int): DataApiResult<Unit>
    suspend fun searchMyBooks(query: String, page: Int?, size: Int?): DataApiResult<MyBookSearchEntity>
    suspend fun getStoreBooks(keyword: String?, page: Int?, size: Int?): DataApiResult<StoreBookEntity>
    suspend fun updateReadingStatus(mybookId: Int, startedDate: String?, finishedDate: String?): DataApiResult<Int>
    suspend fun updateMyBook(mybookId: Int, request: MyBookUpdateEntity): DataApiResult<Int>
    suspend fun saveMyBook(request: SaveMyBookEntity): DataApiResult<Int>
}
