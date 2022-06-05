package app.ronzano.zensino.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.ronzano.zensino.R

class RecalibratePageFragment : Fragment() {
    private var mStep = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val l: Int
        mStep = requireArguments().getInt(ARG_STEP, 0)

        when (mStep) {
            0 -> {
                l = R.layout.fragment_calibrate_1
                mStep = 0
            }
            1 -> l = R.layout.fragment_calibrate_2
            2 -> l = R.layout.fragment_calibrate_3
            3 -> l = R.layout.fragment_calibrate_4
            END_STEP -> l = R.layout.fragment_calibrate_end
            ERROR_STEP -> l = R.layout.fragment_calibrate_error
            else -> {
                l = R.layout.fragment_calibrate_1
                mStep = 0
            }
        }
        return inflater.inflate(l, container, false)
    }

    //    @Optional
    //    @OnClick(R.id.buttonNext)
    fun onNextClick(v: View?) {
        (activity as ICalibrateListener?)!!.next(mStep)
    }

    //    @Optional
    //    @OnClick(R.id.buttonPrevious)
    fun onPreviousClick(v: View?) {
        (activity as ICalibrateListener?)!!.previous(mStep)
    }

    //    @Optional
    //    @OnClick(R.id.buttonCancel)
    fun onCancelClick(v: View?) {
        (activity as ICalibrateListener?)!!.cancel()
    }

    //    @Optional
    //    @OnClick(R.id.buttonEnd)
    fun onEndClick(v: View?) {
        (activity as ICalibrateListener?)!!.end()
    }

    //    @Optional
    //    @OnClick(R.id.buttonCalibrate)
    fun onCalibrateClick(v: View?) {
        (activity as ICalibrateListener?)!!.calibrate()
    }

    companion object {
        const val ARG_STEP = "step"
        const val END_STEP = 100
        const val ERROR_STEP = 200
    }
}