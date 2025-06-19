package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sectionLabel: TextView = itemView.findViewById(R.id.sectionLabelTv)
    private val iv: ImageView = itemView.findViewById(R.id.lineIv)
    val checklistRecyclerView: RecyclerView = itemView.findViewById(R.id.checklistRv)

    init {
        checklistRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
    }

    fun bindData(section: Section) {
        sectionLabel.text = section.label
        iv.setImageResource(R.drawable.line)

        checklistRecyclerView.visibility = if (section.isExpanded) View.VISIBLE else View.GONE

        val adapter = ChecklistAdapter(section.checklist)
        checklistRecyclerView.adapter = adapter
    }
}