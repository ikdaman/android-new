package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.Nickname

interface TestRepository {
    fun checkNickname(nickname: String): Flow<DataResource<Nickname>>
}