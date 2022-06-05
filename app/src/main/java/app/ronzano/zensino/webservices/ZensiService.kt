package app.ronzano.zensino.webservices

import app.ronzano.zensino.webservices.models.*
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

    @GET("api/v2/logo/")
    suspend fun logo(
        @Header("Authorization") token: String,
    ): LogoResponse

    @POST("api/v2/pt/")
    suspend fun telemetry(
        @Header("Authorization") token: String,
        @Body body: Telemetry,
    )

}