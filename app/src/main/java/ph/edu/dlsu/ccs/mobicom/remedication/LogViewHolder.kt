package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class LogViewHolder(itemView: View): ViewHolder(itemView){
    private val logNameTv: TextView = itemView.findViewById(R.id.logNameTv)
    private val logAmountTv: TextView = itemView.findViewById(R.id.logAmountTv)
    private val logDosageTv: TextView = itemView.findViewById(R.id.logDosageTv)
    private val logTimeTv: TextView = itemView.findViewById(R.id.logTimeTv)

    fun bindData(log: Log) {
        logNameTv.text = log.name
        logAmountTv.text = log.amount.toString() + "x "
        logDosageTv.text = log.dosage
        logTimeTv.text = log.time
    }
}