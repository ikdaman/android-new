package project.side.remote.logging

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CompactLoggingInterceptor : Interceptor {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()

        Timber.d("→ %s %s", request.method, request.url)

        // 요청 body 로깅
        val requestBody = request.body
        if (requestBody != null && requestBody.contentType()?.subtype == "json") {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            if (bodyString.length <= MAX_LOG_LENGTH) {
                Timber.d("   req body: %s", bodyString)
            } else {
                var offset = 0
                var part = 1
                while (offset < bodyString.length) {
                    val end = minOf(offset + MAX_LOG_LENGTH, bodyString.length)
                    Timber.d("   req body[%d]: %s", part, bodyString.substring(offset, end))
                    offset = end
                    part++
                }
            }
        }

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Timber.e("← %s %s FAILED: %s", request.method, request.url, e.message)
            throw e
        }

        val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        Timber.d("← %d %s %s (%dms)", response.code, request.method, request.url, durationMs)

        // 응답 본문 전체 로깅 (긴 경우 분할)
        val responseBody = response.body
        if (responseBody != null && responseBody.contentType()?.subtype == "json") {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val bodyString = source.buffer.clone().readUtf8()
            if (bodyString.length <= MAX_LOG_LENGTH) {
                Timber.d("   body: %s", bodyString)
            } else {
                var offset = 0
                var part = 1
                while (offset < bodyString.length) {
                    val end = minOf(offset + MAX_LOG_LENGTH, bodyString.length)
                    Timber.d("   body[%d]: %s", part, bodyString.substring(offset, end))
                    offset = end
                    part++
                }
            }
        }

        return response
    }
}
