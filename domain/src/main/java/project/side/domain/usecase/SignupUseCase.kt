package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.SignupState
import project.side.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        socialToken: String?,
        provider: String?,
        providerId: String?,
        nickname: String?
    ): Flow<SignupState> = authRepository.signup(socialToken, provider, providerId, nickname)
}
