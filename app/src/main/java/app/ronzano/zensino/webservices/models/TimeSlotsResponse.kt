package app.ronzano.zensino.webservices.models

import com.google.gson.annotations.SerializedName

data class TimeSlotsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("timeslots")
    val timeSlots: Array<Array<String>>
)
