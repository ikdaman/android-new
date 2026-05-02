package project.side.remote.auth

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 화이트리스트 경로(예: `/members/me`)에서 토큰 만료가 의심되는 5xx 응답이 오면
 * 응답 코드를 401로 재라벨링해 `TokenAuthenticator`가 reissue 흐름을 발동시키도록 한다.
 *
 * 이 인터셉터는 반드시 application interceptor로 등록되어야 하며
 * `AuthInterceptor`(토큰 부착) **다음**에 위치해야 OkHttp가 응답을 받은 직후
 * 401 매핑된 응답을 보고 authenticator를 호출한다.
 *
 * 무한 재진입 방지: 한 번 재포장된 응답에는 `X-Token-Rescue: 1` 헤더를 부여한다.
 * 동일 요청에 대해 인터셉터가 다시 호출되면 헤더 존재로 감지해 패스스루.
 */
@Singleton
class FiveXxAsUnauthorizedInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.header(HEADER_RESCUE_MARKER) != null) {
            return response
        }

        val path = request.url.encodedPath
        if (!TokenRescuePaths.shouldRescue(path) ||
            !TokenRescuePaths.isRescuableStatus(response.code)
        ) {
            return response
        }

        val rawBody = try {
            response.peekBody(MAX_PEEK_BODY_BYTES).bytes()
        } catch (_: Throwable) {
            ByteArray(0)
        }
        val mediaType = response.body?.contentType()
        response.close()

        return Response.Builder()
            .request(request)
            .protocol(response.protocol ?: Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized (rescued from ${response.code})")
            .header(HEADER_RESCUE_MARKER, "1")
            .body(rawBody.toResponseBody(mediaType))
            .build()
    }

    companion object {
        private const val HEADER_RESCUE_MARKER = "X-Token-Rescue"
        private const val MAX_PEEK_BODY_BYTES = 64L * 1024L
    }
}
