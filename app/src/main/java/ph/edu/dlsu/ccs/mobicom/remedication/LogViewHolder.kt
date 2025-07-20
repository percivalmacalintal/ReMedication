package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class LogViewHolder(itemView: View): ViewHolder(itemView){
    private val logNameTv: TextView = itemView.findViewById(R.id.logNameTv)
    private val logDosageTv: TextView = itemView.findViewById(R.id.logDosageTv)
    private val logTimeTv: TextView = itemView.findViewById(R.id.logTimeTv)
    private val logCardCl: CardView = itemView.findViewById(R.id.logCardCl)

    fun bindData(log: Log) {
        logNameTv.text = log.name
        logDosageTv.text = log.dosage
        logTimeTv.text = log.time

        if (log.getIsMissed()) {
            logCardCl.setCardBackgroundColor(itemView.context.getColor(R.color.lighter_pastel_red))
        } else {
            logCardCl.setCardBackgroundColor(itemView.context.getColor(R.color.lighter_pastel_green)) // or default color
        }

    }
}