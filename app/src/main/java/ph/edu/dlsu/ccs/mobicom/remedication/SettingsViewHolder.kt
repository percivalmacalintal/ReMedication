package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.SharedPreferences
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class SettingsViewHolder(itemView: View): ViewHolder(itemView) {
    private val tv: TextView = itemView.findViewById(R.id.settingsNameTv)
    private val sp: Spinner = itemView.findViewById(R.id.settingsSp)
    private val sharedPreferences: SharedPreferences = itemView.context.getSharedPreferences("ReminderPrefs", 0)

    fun bindData(setting: Setting, position: Int, adapter: SettingsAdapter, onReminderChanged: () -> Unit) {
        tv.text = setting.name

        val spinnerAdapter = ArrayAdapter(
            itemView.context,
            android.R.layout.simple_spinner_item,
            setting.options
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp.adapter = spinnerAdapter
        val savedReminderTime = sharedPreferences.getString(
            setting.name,
            setting.options[2]
        )
        val spinnerPosition = spinnerAdapter.getPosition(savedReminderTime)
        sp.setSelection(spinnerPosition)

        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedReminderTime = parent?.getItemAtPosition(position).toString()

                // Save the selected reminder time in SharedPreferences for this section
                val editor = sharedPreferences.edit()
                editor.putString(setting.name, selectedReminderTime) // Save the reminder time using settings.name as the key
                editor.apply()
                onReminderChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }
}