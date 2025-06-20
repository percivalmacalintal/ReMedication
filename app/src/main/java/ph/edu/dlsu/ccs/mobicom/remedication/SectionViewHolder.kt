package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sectionLabel: TextView = itemView.findViewById(R.id.sectionLabelTv)
    private val lineIv: ImageView = itemView.findViewById(R.id.lineIv)
    val dropdownBtn: ImageButton = itemView.findViewById(R.id.dropdownBtn)
    val checklistRecyclerView: RecyclerView = itemView.findViewById(R.id.checklistRv)

    init {
        checklistRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
    }

    fun bindData(section: Section) {
        sectionLabel.text = section.label
        lineIv.setImageResource(R.drawable.line)
        dropdownBtn.setImageResource(R.drawable.dropdown)

        checklistRecyclerView.visibility = if (section.isExpanded) View.VISIBLE else View.GONE
        dropdownBtn.rotation = if (section.isExpanded) 180f else 0f

        val adapter = ChecklistAdapter(section.checklist)
        checklistRecyclerView.adapter = adapter
    }
}