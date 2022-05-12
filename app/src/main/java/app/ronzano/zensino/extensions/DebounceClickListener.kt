package app.ronzano.zensino.extensions

import android.view.View
import app.ronzano.zensino.R

fun View.debounceClickListener(time: Int = 300, function: (view: View) -> Unit) {
    setOnClickListener {
        val lastTime: Long = (this.getTag(R.id.debounce) ?: 0L) as Long
        val now = System.currentTimeMillis()
        if (now - lastTime > time) {
            setTag(R.id.debounce, now)
            function.invoke(it)
        }
    }
    isClickable = true
}

fun View.removeClickListener() {
    setOnClickListener(null)
    isClickable = false
}
