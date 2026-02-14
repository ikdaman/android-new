package project.side.data.datasource

import project.side.data.model.DataApiResult
import project.side.data.model.MemberEntity
import project.side.data.model.NicknameCheckEntity

interface MemberDataSource {
    suspend fun getMyInfo(): DataApiResult<MemberEntity>
    suspend fun updateNickname(nickname: String): DataApiResult<MemberEntity>
    suspend fun withdraw(): DataApiResult<Unit>
    suspend fun checkNickname(nickname: String): DataApiResult<NicknameCheckEntity>
}
