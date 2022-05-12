package app.ronzano.zensino.models

data class SensorState(var triggered: Boolean, var snoozedUntil: Long? = null)