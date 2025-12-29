package project.side.data.repository

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import project.side.data.datasource.TestDataSource
import project.side.domain.DataResource
import project.side.domain.repository.TestRepository
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val testDataSource: TestDataSource
): TestRepository {
    override fun checkNickname(nickname: String) = flow {
        emit(DataResource.Loading())
        val result = testDataSource.checkNickname(nickname)
        emit(DataResource.Success(result.toDomain()))
    }.catch { e ->
        emit(DataResource.Error(e))
    }
}