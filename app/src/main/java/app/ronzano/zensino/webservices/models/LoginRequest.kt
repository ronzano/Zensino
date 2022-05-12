package app.ronzano.zensino.webservices.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    private val username: String,

    @SerializedName("password")
    private var password: String
)
