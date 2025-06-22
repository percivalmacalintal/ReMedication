package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LogsAdapter(private val data: ArrayList<Log>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (data.isNotEmpty()){
            val view = inflater.inflate(R.layout.log_layout, parent, false)
            return LogViewHolder(view)
        }
        else{
            val view = inflater.inflate(R.layout.nolog_layout, parent, false)
            return NoLogViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LogViewHolder && data.isNotEmpty()) {
            val log = data[position]
            holder.bindData(log)
        }
    }

    override fun getItemCount(): Int {
        return if (data.isEmpty()) 1 else data.size
    }
}