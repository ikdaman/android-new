package project.side.data.repository

import kotlinx.coroutines.flow.Flow
import project.side.data.datasource.AuthDataStoreSource
import project.side.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authDataStoreSource: AuthDataStoreSource
) : UserRepository {
    override fun isLoggedIn(): Flow<Boolean> {
        return authDataStoreSource.isLoggedIn()
    }
}