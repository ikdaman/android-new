package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.Member
import project.side.domain.model.NicknameCheck

interface MemberRepository {
    fun getMyInfo(): Flow<DataResource<Member>>
    fun updateNickname(nickname: String): Flow<DataResource<Member>>
    fun withdraw(): Flow<DataResource<Unit>>
    fun checkNickname(nickname: String): Flow<DataResource<NicknameCheck>>
}
