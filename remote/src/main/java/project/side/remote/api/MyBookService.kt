package project.side.remote.api

import project.side.remote.model.mybook.MyBookDetailResponse
import project.side.remote.model.mybook.MyBookIdResponse
import project.side.remote.model.mybook.MyBookSearchResponse
import project.side.remote.model.mybook.MyBookUpdateRequest
import project.side.remote.model.mybook.ReadingStatusRequest
import project.side.remote.model.mybook.StoreBookResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface MyBookService {
    @GET("/mybooks/{mybookId}")
    suspend fun getMyBookDetail(@Path("mybookId") mybookId: Int): Response<MyBookDetailResponse>

    @DELETE("/mybooks/{mybookId}")
    suspend fun deleteMyBook(@Path("mybookId") mybookId: Int): Response<Unit>

    @GET("/mybooks")
    suspend fun searchMyBooks(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<MyBookSearchResponse>

    @GET("/mybooks/store")
    suspend fun getStoreBooks(
        @Query("keyword") keyword: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<StoreBookResponse>

    @PATCH("/mybooks/{mybookId}/reading-status")
    suspend fun updateReadingStatus(
        @Path("mybookId") mybookId: Int,
        @Body request: ReadingStatusRequest
    ): Response<MyBookIdResponse>

    @PATCH("/mybooks/{mybookId}")
    suspend fun updateMyBook(
        @Path("mybookId") mybookId: Int,
        @Body request: MyBookUpdateRequest
    ): Response<MyBookIdResponse>
}
