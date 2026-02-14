package project.side.domain.usecase.auth

import project.side.domain.repository.AuthRepository
import javax.inject.Inject

class GetProviderUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? {
        return authRepository.getProvider()
    }
}
