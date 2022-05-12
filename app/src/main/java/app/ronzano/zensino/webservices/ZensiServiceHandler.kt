package app.ronzano.zensino.webservices

import app.ronzano.zensino.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ZensiServiceHandler {

    companion object {
        private var client: OkHttpClient = OkHttpClient.Builder().apply {
//            callTimeout(30, TimeUnit.SECONDS)
            retryOnConnectionFailure(false)
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            addInterceptor(HeadersInterceptor())
            addInterceptor(ErrorInterceptor())
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
//                if (Debug.mockApi)
//                addInterceptor(MockInterceptor())
            }
        }.build()

        private fun getBuilder(
            baseUrl: String,
            isJSON: Boolean = true,
        ): Retrofit.Builder {
            return Retrofit.Builder().apply {
                baseUrl(baseUrl)
                if (isJSON) addConverterFactory(GsonConverterFactory.create())
                client(client)
            }
        }

        fun zensiService(baseUrl: String): ZensiService {
            val retrofit = getBuilder(baseUrl).build()
            return retrofit.create(ZensiService::class.java)
        }

    }
}
