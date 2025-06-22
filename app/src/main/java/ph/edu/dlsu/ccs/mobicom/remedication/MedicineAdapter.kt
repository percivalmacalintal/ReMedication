package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView.Adapter
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.MedicineLayoutBinding

class MedicineAdapter(private val data: ArrayList<Medicine>, private val myActivityResultLauncher: ActivityResultLauncher<Intent>
): Adapter<MedicineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemViewBinding: MedicineLayoutBinding = MedicineLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myViewHolder = MedicineViewHolder(itemViewBinding)
        myViewHolder.itemView.setOnClickListener {
            val intent = Intent(myViewHolder.itemView.context, InfoActivity::class.java)
            intent.putExtra(InfoActivity.IMAGE_KEY, data[myViewHolder.absoluteAdapterPosition].imageId)
            intent.putExtra(InfoActivity.NAME_KEY, data[myViewHolder.absoluteAdapterPosition].name)
            intent.putExtra(InfoActivity.DOSAGE_KEY, data[myViewHolder.absoluteAdapterPosition].dosage)
            intent.putExtra(InfoActivity.UNIT_KEY, data[myViewHolder.absoluteAdapterPosition].unit)
            intent.putExtra(InfoActivity.FREQUENCY_KEY, data[myViewHolder.absoluteAdapterPosition].frequency)
            intent.putIntegerArrayListExtra(InfoActivity.TIMEOFDAY_KEY, ArrayList(data[myViewHolder.absoluteAdapterPosition].timeOfDay))
            intent.putExtra(InfoActivity.REMAINING_KEY, data[myViewHolder.absoluteAdapterPosition].remaining)
            intent.putExtra(InfoActivity.START_KEY, data[myViewHolder.absoluteAdapterPosition].getFormattedStartDate())
            intent.putExtra(InfoActivity.END_KEY, data[myViewHolder.absoluteAdapterPosition].getFormattedEndDate())
            intent.putExtra(InfoActivity.POSITION_KEY, myViewHolder.absoluteAdapterPosition)
            myActivityResultLauncher.launch(intent)
        }
        return myViewHolder
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}