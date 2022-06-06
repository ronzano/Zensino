package app.ronzano.zensino

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import app.ronzano.zensino.webservices.models.Telemetry
import kotlin.math.roundToInt

object TelemetryManager {
    @Synchronized
    fun getTelemetry(context: Context): Telemetry {
        val batteryLevel = getBatteryLevel(context)
        val wifiStrength = getWifiStrength(context)
        return Telemetry("" + batteryLevel, "" + wifiStrength)
    }

    private fun getWifiStrength(context: Context): Int {
        val wifiManager: WifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        return WifiManager.calculateSignalLevel(wifiInfo.rssi, 100)
    }

    private fun getBatteryLevel(context: Context): Int {
        val batteryStatus: Intent? =
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        var percentage = -1
        if (batteryStatus != null) {
            val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 1)
            percentage = (level / scale.toFloat() * 100).roundToInt()
        }
        return percentage
    }
}