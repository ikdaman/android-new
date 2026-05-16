package project.side.remote.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TokenAuthenticator 의 핵심 분기 로직만 단위 테스트한다.
 * Authenticator 의 authenticate() 전체 흐름은 OkHttp Call/Response 의존성이 커서
 * MockWebServer 기반 instrumented 영역. 여기서는 분기 판정만 검증.
 */
class TokenAuthenticatorTest {

    @Test
    fun `401 403 404 reissue 응답은 토큰 클리어 트리거`() {
        assertTrue(TokenAuthenticator.shouldClearTokenOnReissueFailure(401))
        assertTrue(TokenAuthenticator.shouldClearTokenOnReissueFailure(403))
        assertTrue(TokenAuthenticator.shouldClearTokenOnReissueFailure(404))
    }

    @Test
    fun `200 과 5xx 는 토큰 유지`() {
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(200))
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(500))
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(502))
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(503))
    }

    @Test
    fun `다른 4xx 는 토큰 유지`() {
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(400))
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(409))
        assertFalse(TokenAuthenticator.shouldClearTokenOnReissueFailure(429))
    }
}
