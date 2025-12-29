package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.Nickname
import project.side.domain.repository.TestRepository
import javax.inject.Inject

class TestUseCase @Inject constructor(
    private val testRepository: TestRepository
){
    operator fun invoke(nickname: String): Flow<DataResource<Nickname>> {
        return testRepository.checkNickname(nickname)
    }
}