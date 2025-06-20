package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LogsDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val logDateLabel: TextView = itemView.findViewById(R.id.sectionLabelTv)
    private val iv: ImageView = itemView.findViewById(R.id.lineIv)
    val logDateRecyclerView: RecyclerView = itemView.findViewById(R.id.checklistRv)
    init {
        logDateRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
    }
    fun bindData(logsDate: LogsDate) {
        logDateLabel.text = logsDate.getFormattedDate()
        iv.setImageResource(R.drawable.line)

        logDateRecyclerView.visibility = if (logsDate.getIsExpanded()) View.VISIBLE else View.GONE

        val adapter = LogsAdapter(logsDate.logs)
        logDateRecyclerView.adapter = adapter
    }
}