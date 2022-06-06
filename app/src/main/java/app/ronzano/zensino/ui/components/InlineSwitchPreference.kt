package app.ronzano.zensino.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import app.ronzano.zensino.R

class InlineSwitchPreference(context: Context?, attrs: AttributeSet?) :
    SwitchPreference(context, attrs) {

    override fun onAttached() {
        super.onAttached()
        layoutResource = R.layout.inline_switchpreference
    }

    //    @Override
    //    protected void onAttachedToActivity() {
    //        super.onAttachedToActivity();
    //        setLayoutResource(R.layout.inline_switchpreference);
    //    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val textViewTitle: TextView = holder.findViewById(R.id.textViewTitle) as TextView
        val textViewSummary: TextView = holder.findViewById(R.id.textViewSummary) as TextView
        val switchValue: SwitchCompat = holder.findViewById(R.id.switchValue) as SwitchCompat
        textViewTitle.text = title
        textViewSummary.text = summary
        switchValue.isChecked = isChecked
        switchValue.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            onPreferenceChangeListener.onPreferenceChange(
                this@InlineSwitchPreference,
                b
            )
        }
    }

//    @Override
    //    protected void onBindView(View rootView) {
    //        super.onBindView(rootView);
    //        TextView textViewTitle = rootView.findViewById(R.id.textViewTitle);
    //        TextView textViewSummary = rootView.findViewById(R.id.textViewSummary);
    //        SwitchCompat switchValue = rootView.findViewById(R.id.switchValue);
    //        textViewTitle.setText(getTitle());
    //        textViewSummary.setText(getSummary());
    //        switchValue.setChecked(isChecked());
    //        switchValue.setOnCheckedChangeListener((compoundButton, b) -> {
    //            getOnPreferenceChangeListener().onPreferenceChange(InlineSwitchPreference.this, b);
    //        });
    //    }
}