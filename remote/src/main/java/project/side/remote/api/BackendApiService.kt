package project.side.remote.api

import project.side.data.model.DataManualBookInfo
import project.side.remote.model.SaveManualBookResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendApiService {
    @POST("/mybooks")
    suspend fun saveManualBookInfo(@Body dataManualBookInfo: DataManualBookInfo): Response<SaveManualBookResponse>
}
