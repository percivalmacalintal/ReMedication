package ph.edu.dlsu.ccs.mobicom.remedication

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.MedicineLayoutBinding

class MedicineViewHolder(private val viewBinding: MedicineLayoutBinding): ViewHolder(viewBinding.root){
    fun bindData(medicine: Medicine) {
        this.viewBinding.MedicineIv.setImageResource(medicine.imageId)
        this.viewBinding.nameTv.text = medicine.name
        val remaining = medicine.remaining.toString() + " remaining"
        this.viewBinding.remainTv.text = remaining
    }
}