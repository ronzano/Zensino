package app.ronzano.zensino.webservices.models

import retrofit2.HttpException

class ZensiApiException(
    override val message: String,
    override val cause: HttpException,
    val httpStatusCode: Int = cause.code()
) :
    Exception(message, cause) {

    override fun getLocalizedMessage(): String {
        return message
    }
}