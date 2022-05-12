package app.ronzano.zensino.webservices

import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
//        if (response.code >= 400) {
//            FirebaseCrashlytics.getInstance().log(request.url.toString())
//        }
        return response
    }

}