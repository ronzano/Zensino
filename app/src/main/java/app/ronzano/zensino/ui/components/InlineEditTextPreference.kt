package app.ronzano.zensino.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceViewHolder
import app.ronzano.zensino.R

class InlineEditTextPreference : EditTextPreference {
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onAttached() {
        super.onAttached()
        layoutResource = R.layout.inline_listpreference
    }

//    override fun onAttachedToActivity() {
//        super.onAttachedToActivity()
//    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val textViewTitle: TextView? = holder?.findViewById(R.id.textViewTitle) as TextView?
        val textViewValue: TextView? = holder?.findViewById(R.id.textViewValue) as TextView?
        textViewTitle?.setText(title)
        textViewValue?.setText(text)
    }

//    override fun onBindView(rootView: View) {
//        super.onBindView(rootView)
//        val textViewTitle: TextView = rootView.findViewById(R.id.textViewTitle)
//        val textViewValue: TextView = rootView.findViewById<TextView>(R.id.textViewValue)
//        textViewTitle.setText(title)
//        textViewValue.setText(text)
//    }
}