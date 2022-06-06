package app.ronzano.zensino.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import app.ronzano.zensino.R
import app.ronzano.zensino.databinding.FragmentRecalibrateBinding

class RecalibrateFragment : Fragment(), ICalibrateListener {

    private lateinit var binding: FragmentRecalibrateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRecalibrateBinding.inflate(inflater, container, false).also {
            binding = it
//            it.model = model
//            it.lifecycleOwner = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showStep(0)
    }

    private fun showStep(step: Int) {
        binding.dots.visibility = if (step <= 3) View.VISIBLE else View.GONE
        for (i in 0 until binding.dots.childCount) {
            val src: Int = if (i > step) R.drawable.dot else R.drawable.dot_selected
            (binding.dots.getChildAt(i) as ImageView).setImageResource(src)
        }
        val f: Fragment = getCalibrateFragment(step)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, f)
            .commit()
    }

    private fun getCalibrateFragment(step: Int): Fragment {
        val f: Fragment = RecalibrateFragment()
        val args = Bundle()
        args.putInt(ARG_STEP, step)
        f.arguments = args
        return f
    }

    companion object {
        const val ARG_STEP = "step"
    }

    override fun previous(currentStep: Int) {
        TODO("Not yet implemented")
    }

    override fun next(currentStep: Int) {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun end() {
        TODO("Not yet implemented")
    }

    override fun calibrate() {
        TODO("Not yet implemented")
    }

}

interface ICalibrateListener {
    fun previous(currentStep: Int)
    fun next(currentStep: Int)
    fun cancel()
    fun end()
    fun calibrate()
}