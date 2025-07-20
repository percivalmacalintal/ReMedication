package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LogsDateAdapter (private val logsDates: List<LogsDate>) :
    RecyclerView.Adapter<LogsDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_layout, parent, false)
        return LogsDateViewHolder(view)  // Using LogsDateViewHolder from the other file
    }

    override fun onBindViewHolder(holder: LogsDateViewHolder, position: Int) {
        val logsDate = logsDates[position]
        holder.bindData(logsDate)  // Use bindData method from LogsDateViewHolder

        val adapter = LogsAdapter(logsDate.logs)
        holder.logDateRecyclerView.adapter = adapter

        holder.itemView.setOnClickListener {
            logsDate.flipIsExpanded()
            notifyItemChanged(position)
        }
        holder.dropdownBtn.setOnClickListener {
            logsDate.flipIsExpanded()
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = logsDates.size
}