package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sectionLabelTv: TextView = itemView.findViewById(R.id.sectionLabelTv)
    val dropdownBtn: ImageButton = itemView.findViewById(R.id.dropdownBtn)
    val checklistRv: RecyclerView = itemView.findViewById(R.id.checklistRv)

    init {
        checklistRv.layoutManager = LinearLayoutManager(itemView.context)
    }

    fun bindData(section: Section) {
        sectionLabelTv.text = section.label

        checklistRv.visibility = if (section.isExpanded) View.VISIBLE else View.GONE
        dropdownBtn.rotation = if (section.isExpanded) 180f else 0f

        val adapter = ChecklistAdapter(section.checklist)
        checklistRv.adapter = adapter
    }
}