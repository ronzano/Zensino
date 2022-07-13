package app.ronzano.zensino.webservices.models

import android.os.Parcelable
import app.ronzano.zensino.Consts
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class StatusResponse(
    @SerializedName("result")
    var result: String,

    @SerializedName("plugins")
    var plugins: ArrayList<String?>? = null,

    @SerializedName("user")
    val user: String,

    @SerializedName("num_sensors")
    val numSensors: Int = 0,

    @SerializedName("dept_name")
    val deptName: String,

    @SerializedName("alert-time-labels")
    val alertTimeLabels: ArrayList<String>,

    @SerializedName("alert-time-colors")
    val alertTimeColors: ArrayList<String>,

    @SerializedName("button-status-colors")
    val buttonStatusColors: HashMap<String, String>? = null,

    @SerializedName("data")
    val data: HashMap<String, HashMap<String, SensorData>>? = null,

//    private Boolean show_facility_header;
    @SerializedName("app-parametes")
    val appParameters: AppParameters? = null
) : Parcelable {

    @Parcelize
    data class SensorData(

        @SerializedName("display_name")
        var displayName: String? = null,
        //    private String facility_name;
        @SerializedName("pad_id")
        val padId: String,

        @SerializedName("pad_id_label")
        private val padIdLabel: String? = null,

        @SerializedName("latest")
        val latest: Date? = null,

        //    private int min_since_latest;
        @SerializedName("min_since_latest_above_th")
        val minSinceLatestAboveTh: Int = 0,

        @SerializedName("min_since_latest_below_threshold")
        val minSinceLatestBelowThreshold: Int = 0,

        @SerializedName("latest_alert_triggered_timestamp")
        val latestAlertTriggeredTimestamp: String? = null,

        @SerializedName("status")
        val status: String? = null,

        @SerializedName("alert_triggered")
        val alertTriggered: Boolean = false,

        @SerializedName("monitor_on")
        val monitorOn: Boolean = false,

        @SerializedName("time_slot_1")
        val timeSlot1: Boolean = false,

        @SerializedName("time_slot_2")
        val timeSlot2: Boolean = false,

        @SerializedName("time_slot_3")
        val timeSlot3: Boolean = false,

        @SerializedName("time_slot_4")
        val timeSlot4: Boolean = false,

        @SerializedName("time_slot_5")
        val timeSlot5: Boolean = false,

        @SerializedName("alert_time_idx")
        val alertTimeIdx: Int = 0,

        /* App fields */
        private val mShowTime: Boolean = false

    ) : Parcelable {

        fun isUndetected(): Boolean {
            return status == Consts.STATUS_UNDETECTED
        }
    }

    @Parcelize
    data class AppParameters(
        @SerializedName("polling_sec")
        var pollingSecs: Long = Consts.DEFAULT_POLLING_INTERVAL_SECS,

//        @SerializedName("service_polling_sec")
//        var servicePollingSecs: Long = Consts.DEFAULT_SERVICE_POLLING_INTERVAL_SECS //ok

        @SerializedName("disconnect_service_sec")
        var disconnectServiceSecs: Long = Consts.DEFAULT_DISCONNECTION_TIMEOUT_SECS,

        @SerializedName("snooze_sec")
        var snoozeSecs: Long = Consts.DEFAULT_SNOOZE_SECS,

        @SerializedName("telemetry_sec")
        var telemetrySecs: Long = Consts.DEFAULT_SERVICE_TELEMETRY_INTERVAL_SECS

    ) : Parcelable


}
