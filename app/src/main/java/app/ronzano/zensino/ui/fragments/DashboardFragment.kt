package app.ronzano.zensino.ui.fragments

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.generateViewId
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import app.ronzano.zensino.R
import app.ronzano.zensino.databinding.FragmentDashboardBinding
import app.ronzano.zensino.extensions.debounceClickListener
import app.ronzano.zensino.services.StatusService
import app.ronzano.zensino.ui.components.SensorTile
import app.ronzano.zensino.ui.viewmodels.MainViewModel
import app.ronzano.zensino.webservices.models.StatusResponse
import com.google.android.material.snackbar.Snackbar

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val mainModel: MainViewModel by activityViewModels()
    private val _statusReceiver = StatusReceiver()

//    private val model: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDashboardBinding.inflate(inflater, container, false).also {
            binding = it
//            it.model = model
            it.lifecycleOwner = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLogout.debounceClickListener { logout() }
//        renderSensors()
//        observeError(model.error)
    }

    private fun logout() {
        mainModel.token = null
        _statusService?.stop()
        findNavController().setGraph(R.navigation.nav_graph_main)
    }

    private var _statusService: StatusService? = null

    private val _statusServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: StatusService.LocalBinder = service as StatusService.LocalBinder
            _statusService = binder.service
            _statusService?.setToken(mainModel.token)
            _statusService?.startService(Intent(context, StatusService::class.java))

//            _statusService?.setCurrentOds(Pair(meteringModel.selectedOdsDettaglio.value!!.idOds, meteringModel.selectedOdsDettaglio.value!!.id))
//            _statusService?.startReading()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            _statusService = null
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().bindService(
            Intent(requireContext(), StatusService::class.java),
            _statusServiceConnection,
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }

    override fun onResume() {
        super.onResume()
        //TODO: grab last status
        registerReceivers()
    }

    override fun onPause() {
        unregisterReceivers()
        super.onPause()
    }

    override fun onStop() {
        requireActivity().unbindService(_statusServiceConnection)
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
            Snackbar.make(requireView(), status.toString(), Snackbar.LENGTH_SHORT).show()
            renderSensors(status)
        }
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
                                    binding.containerSensors.addView(this, 200, 200)
                                    (layoutParams as? ViewGroup.MarginLayoutParams)?.updateMargins(
                                        top = context.resources.getDimensionPixelSize(R.dimen.margin_half),
                                        left = context.resources.getDimensionPixelSize(R.dimen.margin_half),
                                        bottom = context.resources.getDimensionPixelSize(R.dimen.margin_half),
                                        right = context.resources.getDimensionPixelSize(R.dimen.margin_half)
                                    )
                                }
                            }
                            sensorTile.displayName = sensor.displayName
                            status.buttonStatusColors?.get(sensor.status)?.let {
                                sensorTile.setStatusColor(
                                    it
                                )
                            }
                        }
                    }
                }
            }
        }
    }

//    compassView = CompassView(this)
//    compassView?.run {
//        id = generateViewId()
//    }
//    binding.root.addView(compassView, params)
//    compassView?.let { setItemConstraints(binding.root, it.id, binding.north.id) }

}
