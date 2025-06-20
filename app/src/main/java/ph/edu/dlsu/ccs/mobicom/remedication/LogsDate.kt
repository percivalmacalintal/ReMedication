package ph.edu.dlsu.ccs.mobicom.remedication

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LogsDate (date: String, logs: ArrayList<Log>, isExpanded: Boolean = true){
    private var date: Date = parseDate(date)
    var logs = logs
        private set
    private var isExpanded: Boolean = isSameDayAsToday(this.date)

    private fun parseDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    private fun isSameDayAsToday(targetDate: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = Date()
        cal2.time = targetDate

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
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