package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class SettingsAdapter(private val settings: ArrayList<Settings>): Adapter<SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.settings_layout, parent, false)
        return SettingsViewHolder(view)
    }
    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val setting = settings[position]
        holder.bindData(setting, position, this)
    }

    override fun getItemCount(): Int {
        return settings.size
    }
}