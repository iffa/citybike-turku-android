package xyz.santeri.citybike.http

import com.github.ajalt.timberkt.Timber
import okhttp3.*
import xyz.santeri.citybike.BuildConfig

class MockDataInterceptor : Interceptor {
    companion object {
        const val response = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"nb_4257169\",\"stop_code\":\"1\",\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[22.263973,60.446297]},\"properties\":{\"operator\":\"nextbike\",\"name\":\"Demo Turku\",\"last_seen\":1524723858,\"bikes_avail\":0,\"slots_avail\":8,\"slots_total\":10}}]}"
    }

    override fun intercept(chain: Interceptor.Chain?): Response {
        chain?.let {
            if (BuildConfig.DEBUG) {
                Timber.d { "Returning mock response" }

                return Response.Builder()
                        .code(200)
                        .message(response)
                        .request(it.request())
                        .protocol(Protocol.HTTP_1_1)
                        .body(ResponseBody.create(MediaType.parse("application/json"), response))
                        .addHeader("Content-Type", "application/json")
                        .build()
            }
        }
        return chain?.proceed(chain.request())!!
    }
}