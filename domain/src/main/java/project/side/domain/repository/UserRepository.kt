package project.side.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isLoggedIn(): Flow<Boolean>
}