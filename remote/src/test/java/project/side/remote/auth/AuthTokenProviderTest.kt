package project.side.remote.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.AuthDataStoreSource

class AuthTokenProviderTest {

    private lateinit var authDataStoreSource: AuthDataStoreSource
    private lateinit var authTokenProvider: AuthTokenProvider

    @Before
    fun setUp() {
        authDataStoreSource = mockk()
        authTokenProvider = AuthTokenProvider(authDataStoreSource)
    }

    @Test
    fun `초기 상태에서 getToken은 null을 반환한다`() {
        assertNull(authTokenProvider.getToken())
    }

    @Test
    fun `초기 상태에서 getRefreshTokenSync는 null을 반환한다`() {
        assertNull(authTokenProvider.getRefreshTokenSync())
    }

    @Test
    fun `초기 상태에서 getAuthorizationSync는 null을 반환한다`() {
        assertNull(authTokenProvider.getAuthorizationSync())
    }

    @Test
    fun `updateToken 호출 시 DataStore에서 토큰을 읽어 캐시한다`() = runTest {
        coEvery { authDataStoreSource.getAuthorization() } returns "test_auth_token"
        coEvery { authDataStoreSource.getRefreshToken() } returns "test_refresh_token"

        authTokenProvider.updateToken()

        assertEquals("test_auth_token", authTokenProvider.getToken())
        assertEquals("test_auth_token", authTokenProvider.getAuthorizationSync())
        assertEquals("test_refresh_token", authTokenProvider.getRefreshTokenSync())
    }

    @Test
    fun `saveTokenAndUpdateCache는 DataStore에 저장하고 캐시를 업데이트한다`() = runTest {
        coEvery { authDataStoreSource.saveToken(any(), any()) } returns Unit

        authTokenProvider.saveTokenAndUpdateCache("new_auth", "new_refresh")

        assertEquals("new_auth", authTokenProvider.getToken())
        assertEquals("new_auth", authTokenProvider.getAuthorizationSync())
        assertEquals("new_refresh", authTokenProvider.getRefreshTokenSync())
        coVerify { authDataStoreSource.saveToken("new_auth", "new_refresh") }
    }

    @Test
    fun `clearToken 호출 시 모든 캐시가 null이 된다`() = runTest {
        coEvery { authDataStoreSource.saveToken(any(), any()) } returns Unit
        authTokenProvider.saveTokenAndUpdateCache("auth", "refresh")

        authTokenProvider.clearToken()

        assertNull(authTokenProvider.getToken())
        assertNull(authTokenProvider.getAuthorizationSync())
        assertNull(authTokenProvider.getRefreshTokenSync())
    }

    @Test
    fun `updateToken 후 clearToken하면 토큰이 null이다`() = runTest {
        coEvery { authDataStoreSource.getAuthorization() } returns "auth"
        coEvery { authDataStoreSource.getRefreshToken() } returns "refresh"

        authTokenProvider.updateToken()
        assertEquals("auth", authTokenProvider.getToken())

        authTokenProvider.clearToken()
        assertNull(authTokenProvider.getToken())
    }

    @Test
    fun `saveTokenAndUpdateCache를 연속 호출하면 마지막 값이 캐시된다`() = runTest {
        coEvery { authDataStoreSource.saveToken(any(), any()) } returns Unit

        authTokenProvider.saveTokenAndUpdateCache("auth1", "refresh1")
        authTokenProvider.saveTokenAndUpdateCache("auth2", "refresh2")

        assertEquals("auth2", authTokenProvider.getToken())
        assertEquals("refresh2", authTokenProvider.getRefreshTokenSync())
    }
}
