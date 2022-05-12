package app.ronzano.zensino.webservices.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String? = null
)
