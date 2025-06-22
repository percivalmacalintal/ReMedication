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
                val isMissed = i%3 == 0
                logList.add(Log(currentDate, currentTime, "MED NAME", i, "160mg", isMissed))
            }

            return logList
        }

        fun generateNoData(): ArrayList<Log> {
            val logList = ArrayList<Log>()

            return logList
        }
    }
}