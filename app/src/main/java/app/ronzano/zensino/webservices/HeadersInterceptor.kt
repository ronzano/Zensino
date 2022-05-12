package app.ronzano.zensino.webservices

import app.ronzano.zensino.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest: Request = request.newBuilder()
            .addHeader("Connection", "close")
            .addHeader("x-application-id", BuildConfig.APPLICATION_ID)
            .addHeader("x-application-version-name", BuildConfig.VERSION_NAME)
            .addHeader("x-application-version-code", BuildConfig.VERSION_CODE.toString())
            .build()
        return chain.proceed(newRequest)
    }

}