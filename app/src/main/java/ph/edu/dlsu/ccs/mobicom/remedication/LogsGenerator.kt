package ph.edu.dlsu.ccs.mobicom.remedication

import java.text.SimpleDateFormat
import java.util.*

class LogsGenerator {
    companion object{
        val currentDate = Date()
        val timeFormat = SimpleDateFormat("hh:mm a")
        val currentTime = timeFormat.format(currentDate)

        fun generateData(): ArrayList<Log> {
            val logList = ArrayList<Log>()

            for (i in 1..8) {
                logList.add(Log(currentDate, currentTime, "MED NAME", i))
            }

            return logList
        }
    }
}