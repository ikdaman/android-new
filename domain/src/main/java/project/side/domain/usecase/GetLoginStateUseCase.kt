package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.repository.UserRepository
import javax.inject.Inject

class GetLoginStateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Boolean> = userRepository.isLoggedIn()
}