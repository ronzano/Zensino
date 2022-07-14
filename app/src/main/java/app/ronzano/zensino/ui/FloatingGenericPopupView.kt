package app.ronzano.zensino.ui

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.ronzano.zensino.R
import app.ronzano.zensino.services.StatusService

class FloatingGenericPopupView(private val mContext: Context) : FrameLayout(mContext) {
    private lateinit var _windowManager: FloatingManager
    private var _params: WindowManager.LayoutParams? = null
    private var _title: TextView? = null
    private var _message: TextView? = null
    private var _isShown: Boolean = false
    private var _popupId: Int? = null

    private lateinit var _view: View

    init {
        initView()
    }

    private fun initView() {
        _view = LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_Zensino))
            .inflate(R.layout.floating_generic_popup, null)
        _windowManager = FloatingManager.getInstance(mContext)
        _title = _view.findViewById(R.id.title)
        _message = _view.findViewById(R.id.message)
        _view.findViewById<Button>(R.id.buttonDismiss).setOnClickListener {
            close()
        }
    }

    @Suppress("deprecation")
    private fun showPopup() {
        if (_isShown) return
        _isShown = true
        _params = WindowManager.LayoutParams()
        _params?.apply {
            gravity = Gravity.CENTER
            //Always appears above the application window
            type = if (Build.VERSION.SDK_INT >= 26) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            //Format the picture so that the background is transparent
            format = PixelFormat.RGBA_8888

            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                or
//                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON

            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            if (_view.isAttachedToWindow) {
                _windowManager.removeView(_view)
            }
            _windowManager.addView(_view, this)
        }
    }

    fun show(id: Int, title: String, message: String) {
        _title?.text = title
        _message?.text = message
        _popupId = id
        showPopup()
    }

    private fun close() {
//        UserActionsManager.notifyAction(context, UserActionsManager.ActionType.USER_ALERT_ACTION, "None", UserActionsManager.ACTION_ALERT_NO_CONNECTION_DISMISSED, null);
        _windowManager.removeView(_view)
        _isShown = false
        notifyToService()
    }

    private fun notifyToService() {
        val intent = Intent(StatusService.ACTION_GENERIC)
        intent.putExtra(StatusService.EXTRA_ACTION, StatusService.ACTIONS.DISMISS_POPUP.name)
        intent.putExtra(StatusService.EXTRA_POPUP_ID, _popupId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

}
