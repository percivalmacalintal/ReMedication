package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class MedicineViewHolder(itemView: View): ViewHolder(itemView){
    private val ivMedicine: ImageView = itemView.findViewById(R.id.MedicineIv)
    private val tvName: TextView = itemView.findViewById(R.id.nameTv)
    private val tvRemaining: TextView = itemView.findViewById(R.id.remainTv)

    fun bindData(medicine: Medicine) {
        ivMedicine.setImageResource(medicine.imageId)
        tvName.text = medicine.name
        tvRemaining.text = medicine.remaining
    }
}