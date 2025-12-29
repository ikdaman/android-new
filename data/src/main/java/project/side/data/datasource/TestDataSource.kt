package project.side.data.datasource

import project.side.data.model.NicknameEntity

interface TestDataSource {
    suspend fun checkNickname(nickname: String): NicknameEntity
}