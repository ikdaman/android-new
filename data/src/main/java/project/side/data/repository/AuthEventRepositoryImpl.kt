package project.side.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.side.data.model.AuthEvent
import project.side.data.model.DataAuthEvent
import project.side.domain.model.DomainAuthEvent
import project.side.domain.repository.AuthEventRepository
import javax.inject.Inject

class AuthEventRepositoryImpl @Inject constructor() : AuthEventRepository {
    override fun getAuthEvents(): Flow<DomainAuthEvent> {
        return AuthEvent.events.map { dataEventType ->
            when (dataEventType) {
                DataAuthEvent.LOGIN_REQUIRED -> DomainAuthEvent.LOGIN_REQUIRED
            }
        }
    }
}