package ph.edu.dlsu.ccs.mobicom.remedication

import java.util.*
import java.text.SimpleDateFormat

class Medicine(imageId : Int, name: String, dosage: Int, unit: String, frequency: String, timeOfDay: List<Int>, remaining: Int, start: String, end: String) {
    var imageId = imageId
        private set
    var name = name
        private set
    var dosage = dosage
        private set
    var unit = unit
        private set
    var frequency = frequency
        private set
    var timeOfDay = timeOfDay
        private set
    var remaining = remaining
        private set
    private var start: Date = parseDate(start)
    private var end: Date = parseDate(end)

    private fun parseDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    fun getFormattedStartDate(): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(start)
    }

    fun getFormattedEndDate(): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(end)
    }
}