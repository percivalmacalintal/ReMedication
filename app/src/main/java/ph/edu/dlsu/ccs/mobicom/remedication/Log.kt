package ph.edu.dlsu.ccs.mobicom.remedication

import java.text.SimpleDateFormat
import java.util.*

class Log{
    var id: Long = -1
    var date: Date = Date()
    var time: String = ""
    var name: String = ""
    var dosage: String = ""
    var isMissed: Boolean = true

    constructor(date: String, time: String, name: String, dosage: String, isMissed: Boolean) {
        this.date = parseDate(date)
        this.time = time
        this.name = name
        this.dosage = dosage
        this.isMissed = isMissed
    }

    constructor(id: Long, date: String, time: String, name: String, dosage: String, isMissed: Boolean) {
        this.id = id
        this.date = parseDate(date)
        this.time = time
        this.name = name
        this.dosage = dosage
        this.isMissed = isMissed
    }

    fun getIsMissed(): Boolean{
        return this.isMissed
    }

    fun setIsMissed(isMissed: Boolean){
        this.isMissed = isMissed
    }

    private fun parseDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
}
