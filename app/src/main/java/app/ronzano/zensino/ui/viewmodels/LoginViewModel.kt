package app.ronzano.zensino.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    val loading = MutableLiveData(false)
    var error = MutableLiveData<String>(null)

}