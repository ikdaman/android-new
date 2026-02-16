package project.side.remote.datasource

import project.side.data.datasource.MyBookDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.MyBookDetailEntity
import project.side.data.model.MyBookSearchEntity
import project.side.data.model.MyBookUpdateEntity
import project.side.data.model.SaveMyBookEntity
import project.side.data.model.StoreBookEntity
import project.side.remote.api.MyBookService
import project.side.remote.model.mybook.BookInfoRequest
import project.side.remote.model.mybook.HistoryInfoRequest
import project.side.remote.model.mybook.MyBookUpdateRequest
import project.side.remote.model.mybook.ReadingStatusRequest
import project.side.remote.model.mybook.SaveBookInfoRequest
import project.side.remote.model.mybook.SaveHistoryInfoRequest
import project.side.remote.model.mybook.SaveMyBookRequest
import javax.inject.Inject

class MyBookDataSourceImpl @Inject constructor(
    private val myBookService: MyBookService
) : MyBookDataSource {

    override suspend fun getMyBookDetail(mybookId: Int): DataApiResult<MyBookDetailEntity> {
        return try {
            val response = myBookService.getMyBookDetail(mybookId)
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun deleteMyBook(mybookId: Int): DataApiResult<Unit> {
        return try {
            val response = myBookService.deleteMyBook(mybookId)
            if (response.isSuccessful) {
                DataApiResult.Success(Unit)
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun searchMyBooks(query: String, page: Int?, size: Int?): DataApiResult<MyBookSearchEntity> {
        return try {
            val response = myBookService.searchMyBooks(query, page, size)
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun getStoreBooks(keyword: String?, page: Int?, size: Int?): DataApiResult<StoreBookEntity> {
        return try {
            val response = myBookService.getStoreBooks(keyword, page, size)
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun updateReadingStatus(mybookId: Int, startedDate: String?, finishedDate: String?): DataApiResult<Int> {
        return try {
            val response = myBookService.updateReadingStatus(mybookId, ReadingStatusRequest(startedDate, finishedDate))
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.mybookId)
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun updateMyBook(mybookId: Int, request: MyBookUpdateEntity): DataApiResult<Int> {
        return try {
            val historyInfo = if (request.startedDate != null || request.finishedDate != null) {
                HistoryInfoRequest(
                    startedDate = request.startedDate,
                    endedDate = request.finishedDate
                )
            } else null

            val bookInfo = request.bookInfo?.let {
                BookInfoRequest(
                    title = it.title,
                    author = it.author,
                    publisher = it.publisher,
                    publishDate = it.publishDate,
                    isbn = it.isbn,
                    totalPage = it.totalPage
                )
            }

            val response = myBookService.updateMyBook(
                mybookId,
                MyBookUpdateRequest(
                    reason = request.reason,
                    historyInfo = historyInfo,
                    bookInfo = bookInfo
                )
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.mybookId)
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun saveMyBook(request: SaveMyBookEntity): DataApiResult<Int> {
        return try {
            val apiRequest = SaveMyBookRequest(
                bookInfo = SaveBookInfoRequest(
                    source = request.source,
                    aladinId = request.aladinId,
                    isbn = request.isbn,
                    title = request.title,
                    author = request.author,
                    publisher = request.publisher,
                    description = request.description,
                    totalPage = request.totalPage,
                    publishDate = request.publishDate,
                    coverImage = request.coverImage
                ),
                historyInfo = if (request.startedDate != null || request.finishedDate != null) {
                    SaveHistoryInfoRequest(
                        startedDate = request.startedDate,
                        finishedDate = request.finishedDate
                    )
                } else null,
                reason = request.reason
            )
            val response = myBookService.saveMyBook(apiRequest)
            if (response.isSuccessful) {
                DataApiResult.Success(response.code())
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    private fun mapServerError(code: Int, message: String?): String {
        return when (code) {
            400 -> "잘못된 요청입니다 (HTTP $code)."
            401 -> "인증이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요 (HTTP $code)."
            403 -> "접근 권한이 없습니다 (HTTP $code)."
            404 -> "요청한 리소스를 찾을 수 없습니다 (HTTP $code)."
            in 500..599 -> "서버 내부 오류가 발생했습니다 (HTTP $code)."
            else -> "알 수 없는 서버 오류가 발생했습니다 (HTTP $code: ${message ?: "no message"})"
        }
    }
}
