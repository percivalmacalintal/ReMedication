package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class SettingsViewHolder(itemView: View): ViewHolder(itemView) {
    private val tv: TextView = itemView.findViewById(R.id.settingsNameTv)
    private val sp: Spinner = itemView.findViewById(R.id.settingsSp)

    fun bindData(settings: Settings, position: Int, adapter: SettingsAdapter) {
        tv.text = settings.name

        val spinnerAdapter = ArrayAdapter(
            itemView.context,
            android.R.layout.simple_spinner_item,
            settings.options
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp.adapter = spinnerAdapter
        sp.setSelection(2)
    }
}