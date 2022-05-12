package app.ronzano.zensino.ui

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.ronzano.zensino.R
import app.ronzano.zensino.services.StatusService
import app.ronzano.zensino.webservices.models.StatusResponse
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FloatingPopupView(private val mContext: Context) : FrameLayout(mContext) {
    private lateinit var _windowManager: FloatingManager
    private var _params: WindowManager.LayoutParams? = null
    private var _title: TextView? = null
    private var _time: TextView? = null
    private val _sensors = mutableListOf<Sensor>()
    private var _currentSensor: Sensor? = null

    private lateinit var _view: View

    init {
        initView()
    }

    private fun initView() {
        _view = LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_Zensino))
            .inflate(R.layout.floating_popup, null)
        _windowManager = FloatingManager.getInstance(mContext)
        _title = _view.findViewById(R.id.title)
        _time = _view.findViewById(R.id.time)
        _view.findViewById<Button>(R.id.buttonDismiss).setOnClickListener {
            removeCurrentAlert()
        }
        _view.findViewById<Button>(R.id.buttonSnooze).setOnClickListener {
            snooze()
            renderLastTriggeredSensor()
        }
    }

    private fun removeCurrentAlert() {
        _sensors.removeIf { s -> s.sensor.padId == _currentSensor?.sensor?.padId }
        renderLastTriggeredSensor()
    }

    private fun renderLastTriggeredSensor() {
        _sensors.maxByOrNull { s -> s.triggerTime }.let { sensor ->
            if (sensor == null) {
                close()
                return
            }
            _currentSensor = sensor

            _params = WindowManager.LayoutParams()
            _params?.apply {
                gravity = Gravity.CENTER
                //Always appears above the application window
                type = if (Build.VERSION.SDK_INT >= 26) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                }
                //Format the picture so that the background is transparent
                format = PixelFormat.RGBA_8888

                flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                or
//                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON

                width = LayoutParams.WRAP_CONTENT
                height = LayoutParams.WRAP_CONTENT
                if (_view.isAttachedToWindow) {
                    _windowManager.removeView(_view)
                }
                _windowManager.addView(_view, this)
            }
            _title?.text =
                context.getString(R.string.popup_title, sensor.sensor.displayName, sensor.counter)
            _time?.text = sensor.sensor.latestAlertTriggeredTimestamp?.let {
                try {
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.US)
                    formatter.parse(it)?.let { date ->
                        DateFormat.getDateTimeInstance().format(date)
                    }
                } catch (e: Exception) {
                    "NA"
                }
            } ?: "-"
//                DateFormat.getDateTimeInstance().format(Date())
        }

    }

    private fun snooze() {
        _currentSensor?.let { sensor ->
            val intent = Intent(StatusService.ACTION_GENERIC)
            intent.putExtra(StatusService.EXTRA_ACTION, StatusService.ACTIONS.SNOOZE.name)
            intent.putExtra(StatusService.EXTRA_SENSOR_ID, sensor.sensor.padId)
            intent.putExtra(StatusService.EXTRA_SNOOZED_UNTIL, Date().time + (1 * 60 * 1000))
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            removeCurrentAlert()
        }
    }

    fun show(triggeredSensor: StatusResponse.SensorData) {
        _sensors.find { s -> triggeredSensor.padId == s.sensor.padId }.let { s ->
            if (s == null)
                _sensors.add(Sensor(triggeredSensor))
            else {
                s.sensor = triggeredSensor
                s.counter++
                s.triggerTime = Date().time
            }
        }

        renderLastTriggeredSensor()

    }

    private fun close() {
        _windowManager.removeView(_view)
    }

    data class Sensor(
        var sensor: StatusResponse.SensorData,
        var counter: Int = 1,
        var triggerTime: Long = Date().time
    )

}
