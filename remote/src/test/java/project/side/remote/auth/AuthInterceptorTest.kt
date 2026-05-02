package project.side.remote.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private fun fakeChain(captureRequest: (Request) -> Unit): Interceptor.Chain {
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns Request.Builder().url("https://moabook.shop/members/me").get().build()
        every { chain.proceed(any()) } answers {
            val req = firstArg<Request>()
            captureRequest(req)
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("".toResponseBody("application/json".toMediaType()))
                .build()
        }
        return chain
    }

    @Test
    fun `캐시에 토큰이 있으면 그대로 Authorization 헤더를 부착한다`() {
        val provider = mockk<AuthTokenProvider>(relaxed = true)
        every { provider.getToken() } returns "abc"
        val interceptor = AuthInterceptor(provider)

        var captured: Request? = null
        val chain = fakeChain { captured = it }

        interceptor.intercept(chain)

        assertEquals("Bearer abc", captured!!.header("Authorization"))
        coVerify(exactly = 0) { provider.updateToken() }
    }

    @Test
    fun `캐시가 비어있고 DataStore에서 로드 후 토큰이 있으면 헤더 부착 (자동로그인 hydrate)`() {
        val provider = mockk<AuthTokenProvider>(relaxed = true)
        // 첫 호출은 null, updateToken 후 두 번째 호출은 토큰 반환
        every { provider.getToken() } returnsMany listOf(null, "loaded-from-datastore")
        coEvery { provider.updateToken() } returns Unit
        val interceptor = AuthInterceptor(provider)

        var captured: Request? = null
        val chain = fakeChain { captured = it }

        interceptor.intercept(chain)

        coVerify(exactly = 1) { provider.updateToken() }
        assertEquals("Bearer loaded-from-datastore", captured!!.header("Authorization"))
    }

    @Test
    fun `캐시가 비어있고 DataStore에도 토큰이 없으면 헤더 없이 그대로 진행`() {
        val provider = mockk<AuthTokenProvider>(relaxed = true)
        every { provider.getToken() } returns null
        coEvery { provider.updateToken() } returns Unit
        val interceptor = AuthInterceptor(provider)

        var captured: Request? = null
        val chain = fakeChain { captured = it }

        interceptor.intercept(chain)

        coVerify(exactly = 1) { provider.updateToken() }
        assertNull(captured!!.header("Authorization"))
    }
}
