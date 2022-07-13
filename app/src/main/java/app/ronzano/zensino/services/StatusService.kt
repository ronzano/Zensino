package app.ronzano.zensino.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.ContactsContract
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.ronzano.zensino.Consts
import app.ronzano.zensino.R
import app.ronzano.zensino.extensions.log
import app.ronzano.zensino.extensions.loge
import app.ronzano.zensino.models.SensorState
import app.ronzano.zensino.ui.FloatingPopupView
import app.ronzano.zensino.ui.MainActivity
import app.ronzano.zensino.webservices.Constants
import app.ronzano.zensino.webservices.ZensiRepository
import app.ronzano.zensino.webservices.models.StatusResponse
import kotlinx.coroutines.*
import java.util.*


class StatusService : Service() {

    private var _token: String? = null
    private var _notificationManager: NotificationManager? = null
    private val _binder: IBinder = LocalBinder()
    private var timerJob: Job? = null
    private val _job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + _job)
    private val repo by lazy { ZensiRepository(Constants.API_ENDPOINT) }
    private var _alertPopup: FloatingPopupView? = null
    private var _pollingIntervalSecs = Consts.DEFAULT_POLLING_INTERVAL_SECS

    private var _sensorsMap = HashMap<String, SensorState>()

    private var _actionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTIONS.SNOOZE.name -> {
                    val sensorId = intent.getStringExtra(EXTRA_SENSOR_ID)
                    val snoozedUntil = intent.getLongExtra(EXTRA_SNOOZED_UNTIL, 0)
                    sensorId?.let {
                        _sensorsMap[sensorId] = SensorState(false, snoozedUntil)
                        log(sensorId, "has been snoozed")
                    }
                }
            }
        }
    }

    private fun buildNotification(): Notification {
        val activityIntent = Intent(this, MainActivity::class.java)
        activityIntent.action = Intent.ACTION_MAIN
        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val intentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, intentFlags)

        val stopIntent =
            Intent(this, StatusService::class.java).also { it.putExtra(EXTRA_STOP, 1) }
        val stopPendingIntent =
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setContentTitle(getString(R.string.notification_status_message))
            .setOngoing(true)
            .setPriority(NotificationManagerCompat.IMPORTANCE_MAX)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(activityPendingIntent)
            .setShowWhen(true)
            .addAction(R.drawable.ic_stop, getString(R.string.stop), stopPendingIntent)

        return builder.build()
    }

    override fun onCreate() {
        super.onCreate()

        _notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            _notificationManager!!.createNotificationChannel(mChannel)
        }

        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(_actionReceiver, IntentFilter(ACTION_GENERIC))

        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("Service started")

        if (intent?.getIntExtra(EXTRA_STOP, 0) == 1) {
            stop()
        } else {
            _token = intent?.getStringExtra(EXTRA_TOKEN)
            startStatusPolling()
        }
        return START_NOT_STICKY
    }

    private fun startStatusPolling() {
        log("startStatusPolling")
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.IO) {
            while (true) {
                delay(_pollingIntervalSecs * 1000)
                _token?.let { token ->
                    try {
                        val response = repo.status(token)
                        _lastStatus = response
                        _lastUpdateTime = Date().time
                        //Update polling interval
                        _pollingIntervalSecs =
                            response.appParameters?.pollingSecs ?: _pollingIntervalSecs

                        response.data?.let { data ->
                            data.keys.forEach { keyFacility ->
                                data[keyFacility].let { sensors ->
                                    sensors?.keys?.forEach { keySensor ->
                                        sensors[keySensor]?.let { sensor ->
                                            val savedState = _sensorsMap[sensor.padId]

                                            if (sensor.alertTriggered) {
                                                if (savedState?.triggered != true && !isSnoozed(
                                                        savedState?.snoozedUntil
                                                    )
                                                ) {
                                                    _sensorsMap[sensor.padId] = SensorState(true)
                                                    log(sensor)
                                                    showAlertPopup(sensor)
                                                }
                                            } else {
                                                savedState?.triggered = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                        log(response)
                        val intent = Intent(ACTION_STATUS_UPDATE)
                        intent.putExtra(EXTRA_STATUS, _lastStatus)
                        intent.putExtra(EXTRA_DATE, _lastUpdateTime)
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                    } catch (e: Exception) {
                        loge(e)
//                    model.error.value = e.localizedMessage ?: getString(R.string.error)
                    } finally {
                    }
                }
            }
        }
    }

    private fun isSnoozed(snoozedUntil: Long?): Boolean {
        if (snoozedUntil == null) return false
        return (Date().time < snoozedUntil)
    }

    override fun onBind(intent: Intent): IBinder {
        log("Service onBind()")
//        stopForeground(true)
        return _binder
    }

    override fun onRebind(intent: Intent) {
        log("Service onRebind()")
//        stopForeground(true)
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        log("Service Last client unbound from service")
//        log("Service Starting foreground service")
//        startForeground(NOTIFICATION_ID, notification)
        return true // Ensures onRebind() is called when a client re-binds.
    }

    fun setToken(token: String?) {
        _token = token
    }

    private fun showAlertPopup(sensor: StatusResponse.SensorData) {
        scope.launch(Dispatchers.Main) {
            if (_alertPopup == null) {
                _alertPopup = FloatingPopupView(applicationContext)
            }
            _alertPopup!!.show(sensor)
            wakeUp()
        }
    }

    //TODO: MOVE
    private fun wakeUp() {
        val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "zensi:alert"
        )
        wakeLock.acquire()
    }

    fun stop() {
        log("service stop")
        stopSelf()
    }

    override fun onDestroy() {
        log("service destroy")
        timerJob?.cancel()
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        val service: StatusService
            get() = this@StatusService
    }

    enum class ACTIONS { SNOOZE }

    companion object {
        fun getLastStatus(): Pair<StatusResponse?, Long?> {
            return Pair(_lastStatus, _lastUpdateTime)
        }

        private const val P = ContactsContract.Directory.PACKAGE_NAME
        const val ACTION_STATUS_UPDATE = "${P}.action.status_update"
        const val ACTION_GENERIC = "${P}.action.generic"

        const val EXTRA_TOKEN = "${P}.extra.token"
        const val EXTRA_STOP = "${P}.extra.stop"
        const val EXTRA_STATUS = "${P}.extra.status"
        const val EXTRA_DATE = "${P}.extra.date"
        const val EXTRA_ACTION = "${P}.extra.action"
        const val EXTRA_SENSOR_ID = "${P}.extra.sensor_id"
        const val EXTRA_SNOOZED_UNTIL = "${P}.extra.snoozed_until"

//        private const val EXTRA_STARTED_FROM_NOTIFICATION = "${P}}.extra.started_from_notification"

        private const val NOTIFICATION_ID = 68987774

        private var _lastStatus: StatusResponse? = null
        private var _lastUpdateTime: Long? = null

    }
}