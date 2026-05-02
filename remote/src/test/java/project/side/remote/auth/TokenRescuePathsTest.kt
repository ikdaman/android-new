package project.side.remote.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenRescuePathsTest {

    @Test
    fun `members me 경로는 토큰 구조 5xx를 401로 재포장 대상이다`() {
        assertTrue(TokenRescuePaths.shouldRescue("/members/me"))
        assertTrue(TokenRescuePaths.shouldRescue("/members/me?nocache=1"))
    }

    @Test
    fun `다른 임의의 경로는 재포장 대상이 아니다`() {
        assertFalse(TokenRescuePaths.shouldRescue("/mybooks/store"))
        assertFalse(TokenRescuePaths.shouldRescue("/auth/login"))
        assertFalse(TokenRescuePaths.shouldRescue("/auth/reissue"))
        assertFalse(TokenRescuePaths.shouldRescue("/"))
    }

    @Test
    fun `재포장 트리거 코드 매칭`() {
        assertTrue(TokenRescuePaths.isRescuableStatus(500))
        assertTrue(TokenRescuePaths.isRescuableStatus(502))
        assertTrue(TokenRescuePaths.isRescuableStatus(503))
        assertTrue(TokenRescuePaths.isRescuableStatus(504))
        assertFalse(TokenRescuePaths.isRescuableStatus(200))
        assertFalse(TokenRescuePaths.isRescuableStatus(401))
        assertFalse(TokenRescuePaths.isRescuableStatus(404))
        assertFalse(TokenRescuePaths.isRescuableStatus(400))
    }
}
