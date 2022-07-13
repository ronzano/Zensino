package app.ronzano.zensino

object Consts {

    const val DEFAULT_POLLING_INTERVAL_SECS: Long = 1
    const val DEFAULT_DISCONNECTION_TIMEOUT_SECS: Long = 120
    const val DEFAULT_SERVICE_TELEMETRY_INTERVAL_SECS: Long = 15 * 60
    const val DEFAULT_SNOOZE_SECS: Long = 5 * 60

    /*FEATURES DISABLERS*/
    const val DISABLE_ALARM = false
    const val DISABLE_MAIN_SETTINGS = false
    const val ALWAYS_ENABLE_SETTINGS = false
    const val CALIBRATION_CONFIRM = true

    /*STATUS*/
    const val STATUS_OCCUPIED = "occupied"
    const val STATUS_UNDETECTED = "undetected"
    const val STATUS_STANDBY = "standby"

    /*PLUGINS*/
    const val PLUGIN_NOTIFY_USER_ACTIONS = "notify_user_actions"
    const val PLUGIN_NOTIFY_USER_ALERT_ACTIONS = "notify_user_alert_actions"
    const val PLUGIN_FALSE_ALARM_REPORTING = "false_alarms_reporting"
    const val PLUGIN_ALLOW_INAPP_CHART = "allow_inapp_chart"
    const val PLUGIN_TELEMETRY = "allow_phone_telemetry"
    const val PLUGIN_ALLOW_CONNECTION_LOST_POPUP = "allow_internet_disconnection_popup"
    const val PLUGIN_ALLOW_SENSOR_DISCONNECTION_POPUP = "allow_sensor_disconnection_popup"

}
