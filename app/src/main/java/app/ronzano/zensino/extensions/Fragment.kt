package app.ronzano.zensino.extensions

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import app.ronzano.zensino.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.observeError(message: MutableLiveData<String>) {
    message.observe(viewLifecycleOwner) { v ->
        if (v != null && activity != null) {
            Snackbar.make(activity!!.findViewById(android.R.id.content), v, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red)).show()
            message.postValue(null)
        }
    }
}

fun Fragment.observeSuccess(message: MutableLiveData<String>) {
    message.observe(viewLifecycleOwner) { v ->
        if (v != null && activity != null) {
            Snackbar.make(activity!!.findViewById(android.R.id.content), v, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.green)).show()
            message.postValue(null)
        }
    }
}

fun Fragment.navigate(destination: NavDirections) = with(findNavController()) {
    currentDestination?.getAction(destination.actionId)?.let { navigate(destination) }
}