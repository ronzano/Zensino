package app.ronzano.zensino.extensions

import android.util.Log
import app.ronzano.zensino.BuildConfig

//import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Any.log(vararg messages: Any?) {
    if (BuildConfig.DEBUG) {
        val tag = this.javaClass.simpleName.ifEmpty { "App" }
        Log.d(tag, messages.joinToString("\t"))
    }
}

fun Any.logw(vararg messages: Any?) {
    if (BuildConfig.DEBUG) {
        val tag = this.javaClass.simpleName.ifEmpty { "App" }
        Log.w(tag, messages.joinToString("\t"))
    }
}

fun Any.loge(vararg messages: Any?) {
    if (BuildConfig.DEBUG) {
        val tag = this.javaClass.simpleName.ifEmpty { "App" }
        Log.e(tag, messages.joinToString("\t"))
    }
    messages.filterIsInstance<Throwable>().forEach {
//        FirebaseCrashlytics.getInstance().recordException(it)
        if (BuildConfig.DEBUG) {
            it.printStackTrace()
        }
    }
}