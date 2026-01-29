package project.side.remote.api

import project.side.remote.model.HistoryBookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryService {
    @GET("mybooks/history")
    suspend fun getHistoryBooks(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String
    ): Response<HistoryBookResponse>
}