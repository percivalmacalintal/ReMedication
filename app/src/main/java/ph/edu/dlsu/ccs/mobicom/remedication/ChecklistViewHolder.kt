package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ChecklistViewHolder(itemView: View): ViewHolder(itemView) {
    // In our item layout, we need two references -- an ImageView and a TextView. Please note that
    // the itemView is the RecyclerView -- which has context that we can use to find View instances.
    private val iv: ImageView = itemView.findViewById(R.id.medicineImageIv)
    private val mtv: TextView = itemView.findViewById(R.id.medicineNameTv)
    private val dtv: TextView = itemView.findViewById(R.id.dosageTv)
    private val cb: CheckBox = itemView.findViewById(R.id.checklistCb)

    // This is our own method that accepts a Character object and sets our views' info accordingly.
    fun bindData(checklist: Checklist, position: Int, adapter: ChecklistAdapter) {
        iv.setImageResource(checklist.imageId)
        mtv.text = checklist.medicineName
        dtv.text = checklist.dosage
        cb.isChecked = checklist.isChecked
        if (checklist.isChecked) {
            iv.alpha = 0.5f
            mtv.alpha = 0.5f
            dtv.alpha = 0.5f
            cb.alpha = 0.5f
        } else {
            iv.alpha = 1.0f
            mtv.alpha = 1.0f
            dtv.alpha = 1.0f
            cb.alpha = 1.0f
        }

        cb.setOnClickListener {
            checklist.isChecked = !checklist.isChecked
            adapter.notifyItemChanged(position)
        }
    }
}