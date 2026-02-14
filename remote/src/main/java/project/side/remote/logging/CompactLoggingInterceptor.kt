package project.side.remote.logging

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CompactLoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "HTTP"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()

        Log.d(TAG, "→ ${request.method} ${request.url}")

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "← ${request.method} ${request.url} FAILED: ${e.message}")
            throw e
        }

        val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        Log.d(TAG, "← ${response.code} ${request.method} ${request.url} (${durationMs}ms)")

        return response
    }
}
