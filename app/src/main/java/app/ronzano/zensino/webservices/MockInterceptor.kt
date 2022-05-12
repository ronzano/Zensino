package app.ronzano.zensino.webservices

import app.ronzano.zensino.BuildConfig
import app.ronzano.zensino.ZensiApplication
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import java.io.InputStream
import kotlin.math.absoluteValue
import kotlin.random.Random

class MockInterceptor : Interceptor {

    private val _mocks = listOf(
        MockDef(".*api-token-auth.*", "auth.json.mock"),
        MockDef(".*status.*", "status.json.mock"),
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            Thread.sleep(200)
            val request = chain.request()
            val uri = request.url.toUri().toString()

            val mock = _mocks.find { mockDef ->
                uri.matches(Regex(mockDef.pattern)) &&
                        (mockDef.queryFilter == null || mockDef.queryFilter.any { p ->
                            request.url.queryParameter(p.first) == p.second
                        })
            }

            if (mock == null) {
                return Response.Builder()
                    .code(404)
                    .protocol(Protocol.HTTP_2)
                    .message("Mock response not found")
                    .body("".toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                    .request(chain.request())
                    .build()
            } else {
                var responseString = readMockAsset(mock.response) ?: ""

                responseString =
                    responseString.replace("%RANDINT%", Random.nextInt().absoluteValue.toString())
                responseString =
                    responseString.replace("%RANDBOOL%", Random.nextBoolean().toString())

                return Response.Builder()
                    .code(200)
                    .protocol(Protocol.HTTP_2)
                    .message("Mock response")
                    .body(
                        responseString.toByteArray()
                            .toResponseBody("application/json".toMediaTypeOrNull())
                    )
                    .addHeader("content-type", "application/json")
                    .request(chain.request())
                    .build()
            }
        } else {
            //just to be on safe side.
            throw IllegalAccessError(
                "MockInterceptor is only meant for Testing Purposes and " +
                        "bound to be used only with DEBUG mode"
            )
        }
    }

    private fun readMockAsset(filename: String): String? {
        try {
            val inputStream: InputStream = ZensiApplication.ctx.assets.open("mock/$filename")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

private fun Request.getBodyAsString(): String {
    val requestCopy = this.newBuilder().build()
    val buffer = Buffer()
    requestCopy.body?.writeTo(buffer)
    return buffer.readUtf8()
}

data class MockDef(
    val pattern: String,
    val response: String,
    val queryFilter: List<Pair<String, String>>? = null,
    val postFilter: List<Pair<String, String>>? = null
)
