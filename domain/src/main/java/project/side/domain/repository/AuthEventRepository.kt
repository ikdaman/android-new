package project.side.domain.repository

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.DomainAuthEvent

interface AuthEventRepository {
    fun getAuthEvents(): Flow<DomainAuthEvent>
}