package xyz.santeri.citybike.http

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for setting headers to a request.
 *
 * @param [headers] Headers, as [Pair]s.
 */
class HeaderInterceptor constructor(private vararg val headers: Pair<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {

        val request = chain?.request()
        val builder = request?.newBuilder()

        headers.forEach {
            builder?.addHeader(it.first, it.second)
        }

        builder?.build()

        return chain!!.proceed(request!!)
    }
}