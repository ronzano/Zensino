package app.ronzano.zensino.extensions

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import app.ronzano.zensino.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.initOptionsMenu(
    @MenuRes menuRes: Int? = null,
    overrideOnBackPressed: Boolean = false,
    onItemSelected: ((menuItem: MenuItem) -> Unit)? = null
) {
    val menuHost: MenuHost = requireActivity()
    menuHost.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuRes?.let {
                menuInflater.inflate(menuRes, menu)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when {
                overrideOnBackPressed -> {
                    onItemSelected?.invoke(menuItem)
                }
                menuItem.itemId == android.R.id.home -> {
                    activity?.onBackPressed()
                }
                else -> {
                    onItemSelected?.invoke(menuItem)
                }
            }
            return true
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}

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