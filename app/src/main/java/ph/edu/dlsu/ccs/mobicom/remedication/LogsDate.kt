package ph.edu.dlsu.ccs.mobicom.remedication

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LogsDate (date: String, logs: ArrayList<Log>, isExpanded: Boolean){
    private var date: Date = parseDate(date)
    var logs = logs
        private set
    var isExpanded = isExpanded
        private set

    private fun parseDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getIsExpanded(): Boolean{
        return isExpanded
    }

    fun flipIsExpanded(){
        this.isExpanded = !this.isExpanded
    }
}