package app.ronzano.zensino.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import app.ronzano.zensino.R

class ThresholdPreference : Preference, View.OnClickListener {
    private val buttons = arrayOf<Button?>(null, null, null, null)
    private val cycles = arrayOf<View?>(null, null, null, null)
    private var mSelected = 0
    private var mEnabled = true
    private var mAlertTimeLabels: ArrayList<String>? = null
    private var mAlertTimeColors: ArrayList<String>? = null
    private var mAlertTimeLC: ArrayList<AlertTimeButton>? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onAttached() {
        super.onAttached()
        layoutResource = R.layout.threshold_preference
    }

//    override fun onAttachedToActivity() {
//        super.onAttachedToActivity()
//    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.let { rootView ->
            buttons[0] = rootView.findViewById(R.id.button0) as Button?
            buttons[1] = rootView.findViewById(R.id.button1) as Button?
            buttons[2] = rootView.findViewById(R.id.button2) as Button?
            buttons[3] = rootView.findViewById(R.id.button3) as Button?
            cycles[0] = rootView.findViewById(R.id.cycle0)
            cycles[1] = rootView.findViewById(R.id.cycle1)
            cycles[2] = rootView.findViewById(R.id.cycle2)
            cycles[3] = rootView.findViewById(R.id.cycle3)
            buttons[0]?.setOnClickListener(this)
            buttons[1]?.setOnClickListener(this)
            buttons[2]?.setOnClickListener(this)
            buttons[3]?.setOnClickListener(this)
            rebuildAlertTimeLC()
            updateButtons()
        }
    }

//    override fun onBindView(rootView: View) {
//        super.onBindView(rootView)
//        buttons[0] = rootView.findViewById(R.id.button0)
//        buttons[1] = rootView.findViewById(R.id.button1)
//        buttons[2] = rootView.findViewById(R.id.button2)
//        buttons[3] = rootView.findViewById(R.id.button3)
//        cycles[0] = rootView.findViewById(R.id.cycle0)
//        cycles[1] = rootView.findViewById(R.id.cycle1)
//        cycles[2] = rootView.findViewById(R.id.cycle2)
//        cycles[3] = rootView.findViewById(R.id.cycle3)
//        buttons[0].setOnClickListener(this)
//        buttons[1].setOnClickListener(this)
//        buttons[2].setOnClickListener(this)
//        buttons[3].setOnClickListener(this)
//        rebuildAlertTimeLC()
//        updateButtons()
//    }

    override fun onDependencyChanged(dependency: Preference, disableDependent: Boolean) {
        super.onDependencyChanged(dependency, disableDependent)
    }

    override fun onParentChanged(parent: Preference, disableChild: Boolean) {
        super.onParentChanged(parent, disableChild)
        mEnabled = !disableChild
        if (buttons[0] != null) updateButtons()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button0 -> handleButtonClick(0)
            R.id.button1 -> handleButtonClick(1)
            R.id.button2 -> handleButtonClick(2)
            R.id.button3 -> handleButtonClick(3)
        }
        onPreferenceChangeListener.onPreferenceChange(this@ThresholdPreference, mSelected)
        updateButtons()
    }

    private fun handleButtonClick(buttonIndex: Int) {
        var counter = 0
        for (i in mAlertTimeLC!!.indices) {
            val atb = mAlertTimeLC!![i]
            if (buttonIndex == i) {
                var selected = false
                for (j in atb.alertTimes.indices) {
                    val at = atb.alertTimes[j]
                    if (mSelected == at.index) selected = true
                }
                if (selected) atb.selected = (atb.selected + 1) % atb.alertTimes.size
                mSelected = counter + atb.selected
            }
            counter += atb.alertTimes.size
        }
    }

    fun setValue(value: Int) {
        mSelected = value
        if (buttons[0] != null) updateButtons()
    }

    private fun rebuildAlertTimeLC() {
        mAlertTimeLC = ArrayList()
        var counter = 0
        for (i in mAlertTimeLabels!!.indices) {
            val atb = AlertTimeButton(buttons[i], cycles[i])
            val atl = mAlertTimeLabels!![i].split(",").toTypedArray()
            val atc = mAlertTimeColors!![i].split(",").toTypedArray()
            for (j in atl.indices) {
                val a = AlertTime(counter, atl[j], atc[j])
                atb.addAlertTime(a)
                if (counter == mSelected) atb.selected = j
                counter++
            }
            mAlertTimeLC!!.add(atb)
        }
    }

    private fun updateButtons() {
        for (i in mAlertTimeLC!!.indices) {
            val atb = mAlertTimeLC!![i]
            var selected = false
            for (j in atb.alertTimes.indices) {
                val at = atb.alertTimes[j]
                if (at.index == mSelected) {
                    selected = true
                }
                if (atb.selected == j) {
                    val bg: Int =
                        if (!mEnabled) R.drawable.button_threshold_disabled else if (selected) R.drawable.button_threshold_sel else R.drawable.button_threshold
                    atb.button!!.setBackgroundResource(bg)
                    setButtonColor(
                        atb.button,
                        at.color,
                        ContextCompat.getColor(context, R.color.threshold_no)
                    )
                    setButtonLabel(atb.button, at.label)
                }
            }
            atb.cycle!!.visibility =
                if (atb.alertTimes.size > 1) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setButtonColor(button: Button?, strColor: String, def: Int) {
        val l = button!!.background
        val color = Color.parseColor(strColor)
        l.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun setButtonLabel(button: Button?, label: String) {
        button!!.text = label
    }

    fun setLabels(alertTimeLabels: ArrayList<String>?, alertTimeColors: ArrayList<String>?) {
        mAlertTimeLabels = alertTimeLabels
        mAlertTimeColors = alertTimeColors
    }

    private inner class AlertTimeButton internal constructor(
        val button: Button?,
        val cycle: View?
    ) {
        val alertTimes = ArrayList<AlertTime>()
        var selected = 0
        fun addAlertTime(alertTime: AlertTime) {
            alertTimes.add(alertTime)
        }
    }

    private inner class AlertTime internal constructor(
        val index: Int,
        val label: String,
        val color: String
    )
}