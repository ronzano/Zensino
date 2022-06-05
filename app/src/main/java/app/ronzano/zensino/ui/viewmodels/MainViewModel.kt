package app.ronzano.zensino.ui.viewmodels

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var token: String? = null
    //TODO: timeslots
    var timeSlots: Array<Array<String>> = emptyArray()
    var alertTimeLabels = ArrayList<String>()
    var alertTimeColors = ArrayList<String>()
}