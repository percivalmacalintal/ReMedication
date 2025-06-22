package ph.edu.dlsu.ccs.mobicom.remedication

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        val bgColor = when {
            checklist.isChecked -> 0xFFB5F2B5.toInt()
            checklist.isOverdue -> 0xFFFDBEC7.toInt()
            else            -> 0xFFFFFFFF.toInt()
        }
        val rounded = ContextCompat
            .getDrawable(itemView.context, R.drawable.rounded_corners)!!
            .mutate() as GradientDrawable

        rounded.setColor(bgColor)
        itemView.background = rounded

        val alpha = if (checklist.isChecked) 0.5f else 1.0f
        iv.alpha  = alpha
        mtv.alpha = alpha
        dtv.alpha = alpha
        cb.alpha  = alpha


        cb.setOnClickListener {
            checklist.isChecked = !checklist.isChecked
            adapter.notifyItemChanged(position)
        }
    }
}