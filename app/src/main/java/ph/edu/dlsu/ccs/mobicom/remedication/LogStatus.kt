package ph.edu.dlsu.ccs.mobicom.remedication

enum class LogStatus(val value: Int) {
    ONTIME(0), // On time
    LATE(1),   // Late
    MISSED(2); // Missed

    companion object {
        // Convert an integer value to a LogStatus
        fun fromInt(value: Int): LogStatus {
            return when (value) {
                0 -> ONTIME
                1 -> LATE
                2 -> MISSED
                else -> MISSED // Default to MISSED for invalid values
            }
        }
    }
}