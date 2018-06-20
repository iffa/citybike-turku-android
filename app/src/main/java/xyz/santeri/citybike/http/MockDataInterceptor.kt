package xyz.santeri.citybike.http

import android.content.Context
import com.github.ajalt.timberkt.Timber
import okhttp3.*
import retrofit2.mock.NetworkBehavior
import xyz.santeri.citybike.BuildConfig
import java.io.BufferedReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MockDataInterceptor @Inject constructor(val context: Context) : Interceptor {
    private var successResponse: String = context.assets.open("racks/success.json")
            .bufferedReader().use(BufferedReader::readText)
    private var emptyResponse: String = context.assets.open("racks/empty.json")
            .bufferedReader().use(BufferedReader::readText)

    override fun intercept(chain: Interceptor.Chain?): Response {
        chain?.let {
            @Suppress("ConstantConditionIf")
            if (BuildConfig.MOCK_RESPONSES) {
                val behavior = NetworkBehavior.create()
                behavior.setDelay(2000, TimeUnit.MILLISECONDS)
                behavior.setFailurePercent(15) // Chance of HTTP 500 error
                behavior.setErrorPercent(40) // Chance of getting a response, but it is empty
                behavior.setVariancePercent(50) // Delay variance

                Thread.sleep(behavior.calculateDelay(TimeUnit.MILLISECONDS))

                if (behavior.calculateIsFailure()) {
                    Timber.d { "Returning mock response (failure)" }

                    return Response.Builder()
                            .code(500)
                            .message("Internal Server Error")
                            .protocol(Protocol.HTTP_1_1)
                            .request(it.request())
                            .body(ResponseBody.create(MediaType.parse("text/plain"), "Internal Server Error"))
                            .build()
                } else {
                    val error = behavior.calculateIsError()

                    Timber.d { "Returning mock response (empty response: $error)" }

                    return Response.Builder()
                            .code(200)
                            .message(if (error) emptyResponse else successResponse)
                            .request(it.request())
                            .protocol(Protocol.HTTP_1_1)
                            .body(ResponseBody.create(MediaType.parse("application/json"),
                                    if (error) emptyResponse else successResponse))
                            .addHeader("Content-Type", "application/json")
                            .build()
                }
            }
        }

        return chain?.proceed(chain.request())!!
    }
}