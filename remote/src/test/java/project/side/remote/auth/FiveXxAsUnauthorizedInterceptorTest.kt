package project.side.remote.auth

import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.Interceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FiveXxAsUnauthorizedInterceptorTest {

    private val interceptor = FiveXxAsUnauthorizedInterceptor()

    private fun fakeChain(url: String, code: Int, body: String = ""): Interceptor.Chain {
        val request = Request.Builder().url(url).get().build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("test")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response
        return chain
    }

    @Test
    fun `members me 가 500 이면 응답을 401로 재포장한다`() {
        val chain = fakeChain("https://moabook.shop/members/me", 500, "{}")

        val out = interceptor.intercept(chain)

        assertEquals(401, out.code)
        assertEquals("1", out.header("X-Token-Rescue"))
    }

    @Test
    fun `members me 가 502 503 504 도 재포장 대상이다`() {
        listOf(502, 503, 504).forEach { code ->
            val chain = fakeChain("https://moabook.shop/members/me", code)
            val out = interceptor.intercept(chain)
            assertEquals("code=$code 는 401로 재포장돼야 한다", 401, out.code)
        }
    }

    @Test
    fun `members me 가 200 이면 그대로 둔다`() {
        val chain = fakeChain("https://moabook.shop/members/me", 200, """{"nickname":"x"}""")

        val out = interceptor.intercept(chain)

        assertEquals(200, out.code)
        assertNull(out.header("X-Token-Rescue"))
    }

    @Test
    fun `members me 가 401 이면 그대로 둔다 (이미 authenticator가 처리)`() {
        val chain = fakeChain("https://moabook.shop/members/me", 401)

        val out = interceptor.intercept(chain)

        assertEquals(401, out.code)
        assertNull(out.header("X-Token-Rescue"))
    }

    @Test
    fun `화이트리스트 외 경로의 500은 그대로 둔다`() {
        val chain = fakeChain("https://moabook.shop/mybooks/store", 500)

        val out = interceptor.intercept(chain)

        assertEquals(500, out.code)
    }

    @Test
    fun `이미 재포장 마커가 붙은 응답은 다시 처리하지 않는다 (재진입 방지)`() {
        val request = Request.Builder().url("https://moabook.shop/members/me").get().build()
        val rescuedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(500)
            .message("test")
            .header("X-Token-Rescue", "1")
            .body("".toResponseBody("application/json".toMediaType()))
            .build()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns rescuedResponse

        val out = interceptor.intercept(chain)

        assertEquals(500, out.code)
        assertNotNull(out.header("X-Token-Rescue"))
    }

    @Test
    fun `쿼리 파라미터가 있어도 화이트리스트 매칭한다`() {
        val chain = fakeChain("https://moabook.shop/members/me?bust=1", 500)

        val out = interceptor.intercept(chain)

        assertEquals(401, out.code)
    }
}
