package project.side.domain.usecase

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.DomainAuthEvent
import project.side.domain.repository.AuthEventRepository
import javax.inject.Inject

class GetAuthEventUseCase @Inject constructor(
    private val authEventRepository: AuthEventRepository
) {
    operator fun invoke(): Flow<DomainAuthEvent> {
        return authEventRepository.getAuthEvents()
    }
}