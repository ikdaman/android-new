package project.side.local.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import project.side.data.datasource.AuthDataStoreSource
import javax.inject.Inject


class AuthDataStoreSourceImpl @Inject constructor(
    private val authDataStore: DataStore<Preferences>
): AuthDataStoreSource {
    companion object {
        val PROVIDER_KEY = stringPreferencesKey("provider")
        val AUTHORIZATION_KEY = stringPreferencesKey("Authorization")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh-token")
        val NICKNAME_KEY = stringPreferencesKey("nickname")
    }

    private val nickname: Flow<String?> = authDataStore.data.map { prefs ->
        prefs[NICKNAME_KEY]
    }

    override suspend fun getProvider(): String? = authDataStore.data.first()[PROVIDER_KEY]

    override suspend fun getAuthorization(): String? = authDataStore.data.first()[AUTHORIZATION_KEY]

    override suspend fun getRefreshToken(): String? = authDataStore.data.first()[REFRESH_TOKEN_KEY]

    override suspend fun saveToken(
        authorization: String,
        refreshToken: String
    ) {
        authDataStore.edit { prefs ->
            prefs[AUTHORIZATION_KEY] = authorization
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    override suspend fun saveAuthInfo(
        provider: String,
        authorization: String,
        refreshToken: String,
        nickname: String
    ) {
        authDataStore.edit { prefs ->
            prefs[PROVIDER_KEY] = provider
            prefs[AUTHORIZATION_KEY] = authorization
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[NICKNAME_KEY] = nickname
        }
    }

    suspend fun saveNickname(nickname: String) {
        authDataStore.edit { prefs ->
            prefs[NICKNAME_KEY] = nickname
        }
    }

    override suspend fun clear() {
        authDataStore.edit { it.clear() }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return authDataStore.data.map { prefs ->
            !prefs[AUTHORIZATION_KEY].isNullOrBlank()
        }
    }
}