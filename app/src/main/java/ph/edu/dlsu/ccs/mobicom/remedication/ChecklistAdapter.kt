package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class ChecklistAdapter(private val checklists: ArrayList<Checklist>): Adapter<ChecklistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.checklist_layout, parent, false)
        return ChecklistViewHolder(view)
    }
    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val checklist = checklists[position]
        holder.bindData(checklist, position, this)
    }

    override fun getItemCount(): Int {
        return checklists.size
    }
}