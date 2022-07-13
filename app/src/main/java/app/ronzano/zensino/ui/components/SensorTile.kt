package app.ronzano.zensino.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import app.ronzano.zensino.Consts
import app.ronzano.zensino.R
import app.ronzano.zensino.databinding.SensorTileBinding
import app.ronzano.zensino.webservices.models.StatusResponse.SensorData
import java.text.SimpleDateFormat
import java.util.*

class SensorTile(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private var binding: SensorTileBinding = SensorTileBinding.inflate(LayoutInflater.from(context))
    private var _layout: ViewGroup = binding.viewContainer
    var listener: ISensorTileListener? = null

    private var _sensor: SensorData? = null

    var sensor: SensorData?
        get() = _sensor
        set(value) {
            //Update view here
            _sensor = value
            binding.displayName.text = value?.displayName
            binding.time.text = getElapsedTime()
            updateBlink(value?.alertTriggered == true)
        }

    init {
        addView(_layout)
        //TODO - BEGIN
        //TODO: serve davvero nascosto? non conviene mostrarlo sempre?
        binding.time.isVisible = true
//        binding.viewContainer.setOnClickListener {
//            binding.time.isVisible = !binding.time.isVisible
//        }
        //TODO - END
        binding.viewContainer.setOnLongClickListener {
            sensor?.let {
                listener?.onLongClick(it)
            }
            true
        }

    }

    private fun updateBlink(blink: Boolean) {
        if (blink) {
            val anim = ContextCompat.getDrawable(context, R.drawable.blink) as AnimationDrawable?
            binding.dataContainer.background = anim
            anim?.start()
        } else {
            binding.dataContainer.background =
                ContextCompat.getDrawable(context, R.drawable.sensor_not_triggered)
        }
    }

    @Suppress("DEPRECATION")
    fun setStatusColor(colorCode: String) {
        val color = Color.parseColor(colorCode)
        val bg: Int = R.drawable.background_sensor_tile
        binding.viewContainer.background = ContextCompat.getDrawable(context, bg)
        val l: Drawable = binding.viewContainer.background
        l.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun getElapsedTime(): String {
        _sensor?.let { s ->

            if (s.status == Consts.STATUS_UNDETECTED) {
                return context.getString(R.string.offline)
            }
            if (s.status == Consts.STATUS_STANDBY) {
                return context.getString(R.string.standby)
            }
            val x: Int =
                if (s.status == Consts.STATUS_OCCUPIED) s.minSinceLatestBelowThreshold else s.minSinceLatestAboveTh
            val inOutD: Int =
                if (s.status == Consts.STATUS_OCCUPIED) R.string.inD else R.string.outD
            val inOutHM: Int =
                if (s.status == Consts.STATUS_OCCUPIED) R.string.inHM else R.string.outHM
            val inOutM: Int =
                if (s.status == Consts.STATUS_OCCUPIED) R.string.inM else R.string.outM
            if (x != 999999) {
                val days = x / (24 * 60)
                return if (days >= 1) {
                    context.getString(inOutD, days)
                    //                return inOutD + days + " d.";
                } else {
                    val hours = x / 60
                    val minutes = x % 60
                    if (hours >= 1) {
                        context.getString(inOutHM, hours, minutes)
                        //                    return inOut + hours + "h " + minutes + "m";
                    } else {
                        context.getString(inOutM, minutes)
                        //                    return inOut + minutes + "m";
                    }
                }
            }
        }
        return context.getString(R.string.empty_value)
    }

    fun updateMonitoringStatus(
        alertTimeColors: ArrayList<String>,
        timeSlots: Array<Array<String>>
    ) {
        sensor?.let { s ->
            if (s.monitorOn == true) {
                // MICMOD00962019. Do not draw the alert ball on the button if there is no alert set or if undetected
                // the check on "standby" should be redundant, but left nonetheless
                if (s.alertTimeIdx == 0 || s.status.equals("standby", ignoreCase = true)) {
                    binding.threshold.isVisible = false
                    return
                }

                binding.threshold.setBackgroundResource(R.drawable.circle);
                if (!isInMonitoredHours(timeSlots)|| s.isUndetected() == true) {
                        binding.threshold.setBackgroundResource(R.drawable.circle_monitor_disabled);
                    } else {

                        /*The alert ball must behave like this:
                            If monitoring is off there is no alert ball.
                            If monitoring is on and the timeslot is blocked the alert ball is white
                            If monitoring is on and the timeslot is not blocked the alert ball takes the color passed by status ONLY if alert_time_idx != 0.
                            If alert time idx is 0 it means there is no alert set so the ball must not be drawn

                            If zensi is disconnected the alert ball must be white
                        */
                        if (s.alertTimeIdx < alertTimeColors.size) {
                            val color = Color.parseColor(alertTimeColors.get(s.alertTimeIdx))
                            val l: LayerDrawable =
                                binding.threshold.getBackground().mutate() as LayerDrawable
                            l.getDrawable(1).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                        }

                    }
                binding.threshold.isVisible = true
            } else {
                binding.threshold.isVisible = false
            }
        }

    }

    private fun isInMonitoredHours(timeSlots: Array<Array<String>>): Boolean {
        val cal = Calendar.getInstance()
        val h = cal[Calendar.HOUR_OF_DAY]
        val m = cal[Calendar.MINUTE]
        val current: String =
            ("" + h).padStart(2, '0') + ":" + ("" + m).padStart(2, '0') + ":00"
        if (sensor?.timeSlot1 == false && isTimeBetweenTwoTime(
                timeSlots.get(0).get(0) + ":00",
                timeSlots.get(0).get(1) + ":00",
                current
            )
        ) {
            return false
        }
        if (sensor?.timeSlot2 == false && isTimeBetweenTwoTime(
                timeSlots.get(1).get(0) + ":00",
                timeSlots.get(1).get(1) + ":00",
                current
            )
        ) {
            return false
        }
        if (sensor?.timeSlot3 == false && isTimeBetweenTwoTime(
                timeSlots.get(2).get(0) + ":00",
                timeSlots.get(2).get(1) + ":00",
                current
            )
        ) {
            return false
        }
        if (sensor?.timeSlot4 == false && isTimeBetweenTwoTime(
                timeSlots.get(3).get(0) + ":00",
                timeSlots.get(3).get(1) + ":00",
                current
            )
        ) {
            return false
        }
        if (sensor?.timeSlot5 == false && isTimeBetweenTwoTime(
                timeSlots.get(4).get(0) + ":00",
                timeSlots.get(4).get(1) + ":00",
                current
            )
        ) {
            return false
        }
        return true
    }


    fun isTimeBetweenTwoTime(from: String, to: String, currentTime: String): Boolean {
        try {
            val reg = Regex("^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")
            if (from.matches(reg) && to.matches(reg) && currentTime.matches(reg)) {
                var valid = false

                //Start Time
                val initialTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(from)
                val calendarFrom = Calendar.getInstance()
                calendarFrom.time = initialTime

                //Check Time
                val checkTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(currentTime)
                val calendarCheck = Calendar.getInstance()
                calendarCheck.time = checkTime
                if (checkTime.compareTo(initialTime) < 0) {
                    calendarCheck.add(Calendar.DATE, 1)
                }

                //End Time
                val finalTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(to)
                val calendarTo = Calendar.getInstance()
                calendarTo.time = finalTime
                if (finalTime.compareTo(initialTime) < 0) {
                    calendarTo.add(Calendar.DATE, 1)
                }

//                Log.d(Consts.APP_TAG, "from: " + calendarFrom.getTime().toString());
//                Log.d(Consts.APP_TAG, "chk:  " + calendarCheck.getTime().toString());
//                Log.d(Consts.APP_TAG, "to:   " + calendarTo.getTime().toString());
                val actualTime = calendarCheck.time
                if ((actualTime.after(calendarFrom.time) || actualTime.compareTo(calendarFrom.time) == 0) && actualTime.before(
                        calendarTo.time
                    )
                ) {
                    valid = true
                    return valid
                }
            } else {
                return false
                //            throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}

interface ISensorTileListener {
    fun onLongClick(sensor: SensorData)

}
