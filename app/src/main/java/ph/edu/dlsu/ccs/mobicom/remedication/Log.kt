package ph.edu.dlsu.ccs.mobicom.remedication

import java.text.SimpleDateFormat
import java.util.*

class Log{
    var id: Long = -1
    var date: Date = Date()
    var time: String = ""
    var name: String = ""
    var amount: Int = 0
    var dosage: String = ""
    var isMissed: Boolean = true

    constructor(date: Date, time: String, name: String, amount: Int, dosage: String, isMissed: Boolean) {
        this.date = date
        this.time = time
        this.name = name
        this.amount = amount
        this.dosage = dosage
        this.isMissed = isMissed
    }

    constructor(id: Long, date: Date, time: String, name: String, amount: Int, dosage: String, isMissed: Boolean) {
        this.id = id
        this.date = date
        this.time = time
        this.name = name
        this.amount = amount
        this.dosage = dosage
        this.isMissed = isMissed
    }

//    var date = date
//        private set
//    var time = time
//        private set
//    var name = name
//        private set
//    var amount = amount
//        private set
//    var dosage = dosage
//        private set
//    private var isMissed = isMissed

    fun getIsMissed(): Boolean{
        return this.isMissed
    }

    fun setIsMissed(isMissed: Boolean){
        this.isMissed = isMissed
    }

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
}
