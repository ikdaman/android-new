package project.side.domain.usecase.auth

import project.side.domain.repository.UserRepository
import javax.inject.Inject

class ClearAuthUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.clearAuth()
    }
}
