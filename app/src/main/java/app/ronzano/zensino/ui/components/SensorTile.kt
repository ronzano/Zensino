package app.ronzano.zensino.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
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

class SensorTile(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private var binding: SensorTileBinding = SensorTileBinding.inflate(LayoutInflater.from(context))
    private var _layout: ViewGroup = binding.viewContainer
    var listener: ISensorTileListener? = null

    private var _sensor: SensorData? = null

    var sensor: SensorData?
        get() = _sensor
        set(value) {
            _sensor = value
            binding.displayName.text = value?.displayName
            binding.time.text = getElapsedTime()
            updateBlink(value?.alertTriggered == true)
        }

    init {
        addView(_layout)
        //TODO: serve davvero nascosto? non conviene mostrarlo sempre?
        binding.viewContainer.setOnClickListener {
            binding.time.isVisible = !binding.time.isVisible
        }
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

}

interface ISensorTileListener {
    fun onLongClick(sensor: SensorData)

}
