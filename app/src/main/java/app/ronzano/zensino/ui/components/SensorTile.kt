package app.ronzano.zensino.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.ronzano.zensino.R
import app.ronzano.zensino.databinding.SensorTileBinding

class SensorTile(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private var binding: SensorTileBinding = SensorTileBinding.inflate(LayoutInflater.from(context))
    private var _layout: ViewGroup = binding.viewContainer

    private var _displayName: String? = null
    var displayName: String?
        get() = _displayName
        set(value) {
            _displayName = value
            binding.displayName.setText(value)
        }

    init {
        addView(_layout)
    }

    fun setStatusColor(colorCode: String) {
        val color = Color.parseColor(colorCode)
        val bg: Int = R.drawable.background_sensor_tile
        binding.viewContainer.setBackground(ContextCompat.getDrawable(context, bg))
        val l: Drawable = binding.viewContainer.getBackground()
        l.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }


}