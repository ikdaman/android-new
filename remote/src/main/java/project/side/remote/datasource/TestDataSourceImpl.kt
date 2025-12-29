package project.side.remote.datasource

import android.accounts.NetworkErrorException
import project.side.data.datasource.TestDataSource
import project.side.data.model.NicknameEntity
import project.side.remote.api.TestApiService
import javax.inject.Inject

class TestDataSourceImpl @Inject constructor(
    private val testApiService: TestApiService
): TestDataSource {
    override suspend fun checkNickname(nickname: String): NicknameEntity {
        val response = testApiService.checkNickname(nickname)
        return if(response.isSuccessful) {
            response.body()?.toData() ?: throw NetworkErrorException("Response body is null")
        } else {
            throw NetworkErrorException("Response is not Successful")
        }
    }
}