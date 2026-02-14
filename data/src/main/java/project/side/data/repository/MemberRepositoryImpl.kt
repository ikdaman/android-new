package project.side.data.repository

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import project.side.data.datasource.MemberDataSource
import project.side.data.model.DataApiResult
import project.side.domain.DataResource
import project.side.domain.model.Member
import project.side.domain.model.NicknameCheck
import project.side.domain.repository.MemberRepository
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberDataSource: MemberDataSource
) : MemberRepository {

    override fun getMyInfo() = flow<DataResource<Member>> {
        emit(DataResource.Loading())
        when (val result = memberDataSource.getMyInfo()) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data.toDomain()))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun updateNickname(nickname: String) = flow<DataResource<Member>> {
        emit(DataResource.Loading())
        when (val result = memberDataSource.updateNickname(nickname)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data.toDomain()))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun withdraw() = flow<DataResource<Unit>> {
        emit(DataResource.Loading())
        when (val result = memberDataSource.withdraw()) {
            is DataApiResult.Success -> emit(DataResource.Success(Unit))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }

    override fun checkNickname(nickname: String) = flow<DataResource<NicknameCheck>> {
        emit(DataResource.Loading())
        when (val result = memberDataSource.checkNickname(nickname)) {
            is DataApiResult.Success -> emit(DataResource.Success(result.data.toDomain()))
            is DataApiResult.Error -> emit(DataResource.Error(result.message))
            is DataApiResult.Loading -> {}
        }
    }.catch { emit(DataResource.Error(it.message ?: "네트워크 오류")) }
}
