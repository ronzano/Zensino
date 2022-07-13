package app.ronzano.zensino.ui.viewmodels

import androidx.lifecycle.ViewModel
import app.ronzano.zensino.webservices.models.StatusResponse

class MainViewModel : ViewModel() {

    lateinit var token: String
    var status: StatusResponse? = null
    //TODO: timeslots
    var timeSlots: Array<Array<String>> = emptyArray()
}