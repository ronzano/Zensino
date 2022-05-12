package app.ronzano.zensino.ui

import android.content.Context
import android.view.View
import android.view.WindowManager

class FloatingManager private constructor(context: Context) {

    //Get WindowManager object
    private var mWindowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    companion object : SingletonHolder<FloatingManager, Context>(::FloatingManager)

    /**
     *Add floating window
     * @param view
     * @param params
     * @return
     */
    fun addView(view: View, params: WindowManager.LayoutParams): Boolean {
        try {
            mWindowManager.addView(view, params)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     *Remove the suspended window
     *
     * @param view
     * @return
     */
    fun removeView(view: View): Boolean {
        try {
            mWindowManager.removeView(view)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     *Update suspended window parameters
     *
     * @param view
     * @param params
     * @return
     */
    fun updateView(view: View, params: WindowManager.LayoutParams): Boolean {
        try {
            mWindowManager.updateViewLayout(view, params)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}