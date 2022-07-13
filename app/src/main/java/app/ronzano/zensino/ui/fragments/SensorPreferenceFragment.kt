package app.ronzano.zensino.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import app.ronzano.zensino.R
import app.ronzano.zensino.extensions.initOptionsMenu
import app.ronzano.zensino.ui.components.InlineEditTextPreference
import app.ronzano.zensino.ui.components.ThresholdPreference
import app.ronzano.zensino.ui.viewmodels.MainViewModel
import app.ronzano.zensino.webservices.models.SensorSettings

class SensorPreferenceFragment : PreferenceFragmentCompat() {

    companion object {
//        const val EXTRA_SENSOR = "sensor"
//        const val EXTRA_TIME_LABELS = "time_labels"
//        const val EXTRA_TIME_COLORS = "time_colors"
    }

    private val mainModel: MainViewModel by activityViewModels()
    private var mSensorSettings: SensorSettings? = null
    private val args: SensorPreferenceFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            show()
        }

        val sensor = args.sensor
//        val sensor: StatusResponse.SensorData = requireActivity().getIntent()
//            .getSerializableExtra(EXTRA_SENSOR) as StatusResponse.SensorData
//        val alertTimeLabels = requireActivity().getIntent()
//            .getSerializableExtra(EXTRA_TIME_LABELS) as ArrayList<String>
//        val alertTimeColors = getIntent()
//            .getSerializableExtra(EXTRA_TIME_COLORS) as ArrayList<String>
        addPreferencesFromResource(R.xml.sensor_settings)

        //Dynamic time slots
//        val timeSlots: Array<Array<String>> = PreferencesManager.getTimeSlots(getActivity())
        val preferenceIds = intArrayOf(
            R.string.key_morning,
            R.string.key_lunch,
            R.string.key_afternoon,
            R.string.key_evening,
            R.string.key_night
        )
        for (i in preferenceIds.indices) {
            if (mainModel.timeSlots.size > i) (findPreference(getString(preferenceIds[i])) as? Preference)?.summary =
                mainModel.timeSlots[i][0] + " - " + mainModel.timeSlots[i][1]
        }
        (findPreference(getString(R.string.key_sensor_displayname)) as? InlineEditTextPreference)?.text =
            sensor.displayName
        (findPreference(getString(R.string.key_monitor_enabled)) as? SwitchPreference)?.isChecked =
            sensor.monitorOn
        (findPreference(getString(R.string.key_morning)) as? SwitchPreference)?.isChecked =
            sensor.timeSlot1
        (findPreference(getString(R.string.key_lunch)) as? SwitchPreference)?.isChecked =
            sensor.timeSlot2
        (findPreference(getString(R.string.key_afternoon)) as? SwitchPreference)?.isChecked =
            sensor.timeSlot3
        (findPreference(getString(R.string.key_evening)) as? SwitchPreference)?.isChecked =
            sensor.timeSlot4
        (findPreference(getString(R.string.key_night)) as? SwitchPreference)?.isChecked =
            sensor.timeSlot5
        (findPreference(getString(R.string.key_threshold)) as? ThresholdPreference?)?.apply {
            setLabels(
                mainModel.status?.alertTimeLabels,
                mainModel.status?.alertTimeColors
            )
            setValue(sensor.alertTimeIdx)
        }

        mSensorSettings = SensorSettings(sensor.padId).also {
            it.alertTimeIdx = sensor.alertTimeIdx
            it.displayName = sensor.displayName
            it.isMonitoringActive = sensor.monitorOn
            it.timeSlot1On = sensor.timeSlot1
            it.timeSlot2On = sensor.timeSlot2
            it.timeSlot3On = sensor.timeSlot3
            it.timeSlot4On = sensor.timeSlot4
            it.timeSlot5On = sensor.timeSlot5
        }

        //Ugly code start
        (findPreference(getString(R.string.key_sensor_displayname)) as? InlineEditTextPreference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.displayName = (o as String?)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_monitor_enabled)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.isMonitoringActive = (o as Boolean? == true)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_morning)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.timeSlot1On = (o as Boolean? == true)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_lunch)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.timeSlot2On = (o as Boolean? == true)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_afternoon)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.timeSlot3On = (o as Boolean? == true)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_evening)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.timeSlot4On = (o as Boolean? == true)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_night)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any? ->
            mSensorSettings?.timeSlot5On = (o as Boolean? == true)
            updateSensorSettings()
            true
        }
        (findPreference(getString(R.string.key_threshold)) as? Preference)?.setOnPreferenceChangeListener { _: Preference?, o: Any ->
            mSensorSettings?.alertTimeIdx = (o as Int)
            updateSensorSettings()
            true
        }
        //Ugly code end

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOptionsMenu()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    private fun updateSensorSettings() {
//        val bc: Context = this.getActivity().getBaseContext()
//        ServiceManager.getInstance(bc).changeSensorSettings(
//            bc,
//            mSensorSettings,
//            object : ICallback<SensorSettingsChangeResponse?>() {
//                fun onResult(result: SensorSettingsChangeResponse) {
//                    Log.d(Consts.APP_TAG, result.toString())
//                    val duration: Int = Toast.LENGTH_SHORT
//                    val settings_saved_popup: Toast = Toast.makeText(bc, "Settings saved", duration)
//
//                    // Try to shorten the duration of the popup. Solution found here: https://stackoverflow.com/questions/3775074/set-toast-appear-length/9715422#9715422
//                    val handler = Handler()
//                    handler.postDelayed({ settings_saved_popup.cancel() }, 1000)
//
//                    /*MICMOD: pushing down the popup message so it doesnt interfere too much*/settings_saved_popup.setGravity(
//                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
//                        0,
//                        -30
//                    )
//                    settings_saved_popup.show()
//                }
//
//                fun onError(error: Exception) {
//                    Toast.makeText(bc, error.message, Toast.LENGTH_SHORT).show()
//                }
//            })
    }
}