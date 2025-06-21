package ph.edu.dlsu.ccs.mobicom.remedication

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LogsDateGenerator {
    companion object{
        fun generateData(): ArrayList<LogsDate> {
            val calendar = Calendar.getInstance()
            calendar.time = Date()

            val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

            val tempList = ArrayList<LogsDate>()
            for (i in 1..5) {
                val date = calendar.time
                val formattedDate = formatter.format(date)

                tempList.add(LogsDate(formattedDate, LogsGenerator.generateData()))

                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }
            return tempList
        }
    }
}