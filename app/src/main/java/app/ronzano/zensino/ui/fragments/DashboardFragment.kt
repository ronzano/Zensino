package app.ronzano.zensino.ui.fragments

import android.content.*
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.generateViewId
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import app.ronzano.zensino.Consts
import app.ronzano.zensino.R
import app.ronzano.zensino.databinding.FragmentDashboardBinding
import app.ronzano.zensino.extensions.loge
import app.ronzano.zensino.extensions.navigate
import app.ronzano.zensino.services.StatusService
import app.ronzano.zensino.ui.components.ISensorTileListener
import app.ronzano.zensino.ui.components.SensorTile
import app.ronzano.zensino.ui.viewmodels.MainViewModel
import app.ronzano.zensino.webservices.Constants
import app.ronzano.zensino.webservices.ZensiRepository
import app.ronzano.zensino.webservices.models.StatusResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val mainModel: MainViewModel by activityViewModels()
    private val _statusReceiver = StatusReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        return FragmentDashboardBinding.inflate(inflater, container, false).also {
            binding = it
            it.lifecycleOwner = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = ZensiRepository(Constants.API_ENDPOINT)

        lifecycleScope.launch {
            //TODO: preload cached logo
            mainModel.token.let { t ->
                try {
                    val logoUrl = repo.logo(t).logo
                    Glide
                        .with(this@DashboardFragment)
                        .load(Constants.API_ENDPOINT + logoUrl)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.logoHospital)
                } catch (e: Exception) {
                    loge(e)
                }
            }
        }
    }

    private fun logout() {
//        mainModel.token = null
//        _statusService?.stop()
        findNavController().setGraph(R.navigation.nav_graph_main)
    }

    fun startStatusService() {
        val intent = Intent(context, StatusService::class.java)
        intent.putExtra(StatusService.EXTRA_TOKEN, mainModel.token)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context?.startForegroundService(intent)
        else
            context?.startService(intent)

    }

    override fun onStart() {
        super.onStart()
        startStatusService()
    }

    override fun onResume() {
        super.onResume()
        val ls = StatusService.getLastStatus()
        updateStatus(ls.first, ls.second)
        registerReceivers()
    }

    override fun onPause() {
        unregisterReceivers()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun registerReceivers() {
        unregisterReceivers()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(_statusReceiver, IntentFilter(StatusService.ACTION_STATUS_UPDATE))
    }

    private fun unregisterReceivers() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(_statusReceiver)
    }

    private inner class StatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getParcelableExtra<StatusResponse>(StatusService.EXTRA_STATUS)
            val date = intent.getLongExtra(StatusService.EXTRA_DATE, 0)
            updateStatus(status, date)
        }
    }

    private fun updateStatus(status: StatusResponse?, date: Long?) {
        if (date != null && date != 0L) {
            mainModel.status = status
            updateUI(status, Date(date))
        }
    }

    private fun updateUI(status: StatusResponse?, date: Date) {
        binding.user.text = status?.user ?: getString(R.string.no_user)
        val df = DateFormat.getDateTimeInstance()         //TODO: usare extension
        val lastDate = df.format(date)         //TODO: usare extension
        binding.lastUpdate.text = getString(R.string.last_update, lastDate)

        renderSensors(status)
//        status?.let { s  ->
//            navigate(
//                DashboardFragmentDirections.actionDashboardFragmentToSensorPreferenceFragment(
//                    s.data!!.let {
//                        it.get(it.keys.first())!!.get("3")!!
//                    }
//                )
//            )
//        }

    }

    private fun renderSensors(status: StatusResponse?) {
        //TODO: clear removed sensors (rendered but not in status)
        status?.data?.let { data ->
            data.keys.forEach { keyFacility ->
                data[keyFacility].let { sensors ->
                    sensors?.keys?.forEach { keySensor ->
                        sensors[keySensor]?.let { sensor ->
                            var sensorTile =
                                binding.containerSensors.findViewWithTag<SensorTile>(sensor.padId)
                            if (sensorTile == null) {
                                sensorTile = SensorTile(requireContext()).apply {
                                    id = generateViewId()
                                    tag = sensor.padId
                                    binding.containerSensors.addView(this, 100.px, 100.px)
                                    (layoutParams as? ViewGroup.MarginLayoutParams)?.updateMargins(
                                        top = context.resources.getDimensionPixelSize(R.dimen.margin_half),
                                        left = context.resources.getDimensionPixelSize(R.dimen.margin_half),
                                        bottom = context.resources.getDimensionPixelSize(R.dimen.margin_half),
                                        right = context.resources.getDimensionPixelSize(R.dimen.margin_half)
                                    )
                                    listener = object : ISensorTileListener {
                                        override fun onLongClick(sensor: StatusResponse.SensorData) {
                                            if (!sensor.isUndetected() || Consts.ALWAYS_ENABLE_SETTINGS) { //Disable settings
                                                navigate(
                                                    DashboardFragmentDirections.actionDashboardFragmentToSensorPreferenceFragment(
                                                        sensor
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            sensorTile.sensor = sensor
                            status.buttonStatusColors?.get(sensor.status)?.let {
                                sensorTile.setStatusColor(it)
                            }
                            sensorTile.updateMonitoringStatus(
                                status.alertTimeColors.flat(),
                                mainModel.timeSlots
                            )
                        }
                    }
                }
            }
        }
    }

}

private fun ArrayList<String>.flat(): ArrayList<String> {
    val result = ArrayList<String>()
    for (at in this) {
        val flat = at.split(",").toTypedArray()
        result.addAll(Arrays.asList(*flat))
    }
    return result
}
