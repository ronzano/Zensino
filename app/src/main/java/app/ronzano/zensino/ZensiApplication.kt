package app.ronzano.zensino

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class ZensiApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
//        lateinit var db: WMSDatabase
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext

//        db = createDb(applicationContext)
    }
}