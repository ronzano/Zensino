package app.ronzano.zensino.webservices.models

import com.google.gson.annotations.SerializedName

data class LogoResponse(
    @SerializedName("logo")
    var logo: String
)
