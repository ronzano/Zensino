package app.ronzano.zensino.webservices.models

import com.google.gson.annotations.SerializedName

class SensorSettings(@SerializedName("pad_id") val padId: String) {
    @SerializedName("display_name")
    var displayName: String? = null

    @SerializedName("is_monitoring_active")
    var isMonitoringActive = false

    @SerializedName("time_slot_1_on")
    var timeSlot1On = false

    @SerializedName("time_slot_2_on")
    var timeSlot2On = false

    @SerializedName("time_slot_3_on")
    var timeSlot3On = false

    @SerializedName("time_slot_4_on")
    var timeSlot4On = false

    @SerializedName("time_slot_5_on")
    var timeSlot5On = false

    @SerializedName("alert_time_idx")
    var alertTimeIdx = 0


}