package app.ronzano.zensino.webservices

import app.ronzano.zensino.webservices.models.LoginRequest
import app.ronzano.zensino.webservices.models.LoginResponse
import app.ronzano.zensino.webservices.models.StatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ZensiService {

    @POST("api-token-auth/")
    suspend fun login(
        @Body body: LoginRequest,
    ): LoginResponse

    @GET("api/v2/status/")
    suspend fun status(
        @Header("Authorization") token: String,
    ): StatusResponse

}