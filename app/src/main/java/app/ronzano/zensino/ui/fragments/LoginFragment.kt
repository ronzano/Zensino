package app.ronzano.zensino.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.ronzano.zensino.BuildConfig
import app.ronzano.zensino.R
import app.ronzano.zensino.databinding.FragmentLoginBinding
import app.ronzano.zensino.extensions.debounceClickListener
import app.ronzano.zensino.extensions.loge
import app.ronzano.zensino.extensions.navigate
import app.ronzano.zensino.extensions.observeError
import app.ronzano.zensino.ui.viewmodels.LoginViewModel
import app.ronzano.zensino.ui.viewmodels.MainViewModel
import app.ronzano.zensino.webservices.Constants
import app.ronzano.zensino.webservices.ZensiRepository
import app.ronzano.zensino.webservices.models.LoginResponse
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var first: Int = 0
    private lateinit var binding: FragmentLoginBinding
    private val mainModel: MainViewModel by activityViewModels()
    private val model: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLoginBinding.inflate(inflater, container, false).also {
            binding = it
            it.model = model
            it.lifecycleOwner = this
            binding.version.text =
                String.format("v%s (%s)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeError(model.error)

        checkFloatingWindow()

        binding.buttonLogin.debounceClickListener { onLoginClick() }
        if (first++ == 0) onLoginClick()

    }

    private fun checkFloatingWindow(): Boolean {
        return if (Settings.canDrawOverlays(context)) {
            true
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
            false
        }
    }

    private fun onLoginClick() {
        if (model.loading.value == true) return
        var error = false
        val username: String = binding.username.text.toString()
        val password: String = binding.password.text.toString()
        if (username.isBlank()) {
            binding.username.error = getString(R.string.username_invalid)
            error = true
        }
        if (password.isBlank()) {
            binding.password.error = getString(R.string.password_invalid)
            error = true
        }
        if (error) return
        model.loading.value = true

        lifecycleScope.launch {
            try {
                val repo = ZensiRepository(Constants.API_ENDPOINT)
                val response = repo.login(username, password)
                onLoggedIn(username, password, response)

            } catch (e: Exception) {
                loge(e)
                model.error.value = e.localizedMessage ?: getString(R.string.error)
            } finally {
                model.loading.value = false
            }
        }
    }

    private fun onLoggedIn(username: String, password: String, response: LoginResponse) {
//        FirebaseCrashlytics.getInstance().setUserId(CurrentConfig.login!!.userId)
        mainModel.token = response.token
        navigate(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())

    }

}
