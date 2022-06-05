package app.ronzano.zensino.webservices.models

import app.ronzano.zensino.BuildConfig
import com.google.gson.annotations.SerializedName

class Telemetry(
    @SerializedName("battery_level") private val batteryLevel: String?,
    @SerializedName("wifi_strength") private val wifiStrength: String
) {
    @SerializedName("app_version")
    private val appVersion = BuildConfig.VERSION_NAME

    override fun toString(): String {
        return "Telemetry{" +
                "batteryLevel='" + batteryLevel + '\'' +
                ", wifiStrength='" + wifiStrength + '\'' +
                ", appVersion='" + appVersion + '\'' +
                '}'
    }
}