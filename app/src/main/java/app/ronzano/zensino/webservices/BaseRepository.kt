package app.ronzano.zensino.webservices

import app.ronzano.zensino.R
import app.ronzano.zensino.ZensiApplication
import app.ronzano.zensino.extensions.loge
import app.ronzano.zensino.webservices.models.ZensiApiException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseRepository {

    protected suspend fun <T> apiCall(call: suspend () -> T): T {
        try {
            return call()
        } catch (e: Exception) {
            val context = ZensiApplication.ctx
            if (e is HttpException) {
                val customException = when (e.code()) {
                    400 -> ZensiApiException(context.getString(R.string.request_error, e.code()), e)
                    401 -> ZensiApiException(
                        context.getString(
                            R.string.unauthorized_error,
                            e.code()
                        ), e
                    )
                    404 -> ZensiApiException(
                        context.getString(R.string.not_found_error, e.code()),
                        e
                    )
                    500, 502 -> ZensiApiException(
                        context.getString(
                            R.string.internal_server_error,
                            e.code()
                        ), e
                    )
                    else -> ZensiApiException(e.localizedMessage ?: e.message(), e)
                }
                loge(customException)
                throw customException
            } else if (e is UnknownHostException) {
                throw Exception(context.getString(R.string.network_error))
            } else if (e is SocketTimeoutException) {
                throw Exception(context.getString(R.string.timeout))
            }
            throw e
        }
    }

}