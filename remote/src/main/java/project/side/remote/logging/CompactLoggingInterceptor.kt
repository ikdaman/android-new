package project.side.remote.logging

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CompactLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()

        Timber.d("→ %s %s", request.method, request.url)

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Timber.e("← %s %s FAILED: %s", request.method, request.url, e.message)
            throw e
        }

        val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        Timber.d("← %d %s %s (%dms)", response.code, request.method, request.url, durationMs)

        // 응답 본문 요약 로깅
        val responseBody = response.body
        if (responseBody != null && responseBody.contentType()?.subtype == "json") {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val bodyString = source.buffer.clone().readUtf8()
            val preview = if (bodyString.length > 200) bodyString.take(200) + "…" else bodyString
            Timber.d("   body: %s", preview)
        }

        return response
    }
}
