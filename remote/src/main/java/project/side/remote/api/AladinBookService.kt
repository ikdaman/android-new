package project.side.remote.api


import project.side.remote.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface AladinBookService {
    @GET("ttb/api/ItemSearch.aspx")
    suspend fun searchBookWithTitle(
        @Query("ttbkey") ttbkey: String = BuildConfig.TTB_KEY,
        @Query("query") query: String,
        @Query("queryType") queryType: String = "Title",
        @Query("cover") cover: String = "Big",
        @Query("output") output: String = "js",
        @Query("version") version: String = "20131101",
        @Query("maxResults") maxResults: Int = 50,
        @Query("start") startPage: Int = 1
    ): BookSearchResponse

    @GET("ttb/api/ItemLookUp.aspx")
    suspend fun searchBookWithIsbn(
        @Query("ttbkey") ttbkey: String = BuildConfig.TTB_KEY,
        @Query("ItemId") itemId: String,
        @Query("itemIdType") itemIdType: String = "ISBN13",
        @Query("cover") cover: String = "Big",
        @Query("output") output: String = "js",
        @Query("Version") version: String = "20131101"
    ): BookSearchResponse
}
