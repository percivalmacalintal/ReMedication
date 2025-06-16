package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SectionAdapter(private val sections: List<Section>) :
    RecyclerView.Adapter<SectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_layout, parent, false)
        return SectionViewHolder(view)  // Using SectionViewHolder from the other file
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bindData(section)  // Use bindData method from SectionViewHolder

        val adapter = ChecklistAdapter(section.checklist)
        holder.checklistRecyclerView.adapter = adapter

        holder.itemView.setOnClickListener {
            section.isExpanded = !section.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = sections.size
}