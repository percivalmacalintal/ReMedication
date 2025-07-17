package ph.edu.dlsu.ccs.mobicom.remedication

import java.util.*
import java.text.SimpleDateFormat

class Medicine{
    var id: Long = -1
    var imageUri: String = ""
    var name: String = ""
    var dosage: Int = 0
    var unit: String = ""
    var frequency: String = ""
    var timeOfDay: List<Int> = emptyList()
    var remaining: Int = 0
    var start: Date = Date()
    var end: Date = Date()

    // Constructor without ID
    constructor(imageUri: String, name: String, dosage: Int, unit: String, frequency: String, timeOfDay: List<Int>, remaining: Int, start: String, end: String) {
        this.imageUri = imageUri
        this.name = name
        this.dosage = dosage
        this.unit = unit
        this.frequency = frequency
        this.timeOfDay = timeOfDay
        this.remaining = remaining
        this.start = parseDate(start)
        this.end = parseDate(end)
    }

    // Constructor with ID
    constructor(id: Long, imageUri: String, name: String, dosage: Int, unit: String, frequency: String, timeOfDay: List<Int>, remaining: Int, start: String, end: String) {
        this.id = id
        this.imageUri = imageUri
        this.name = name
        this.dosage = dosage
        this.unit = unit
        this.frequency = frequency
        this.timeOfDay = timeOfDay
        this.remaining = remaining
        this.start = parseDate(start)
        this.end = parseDate(end)
    }

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