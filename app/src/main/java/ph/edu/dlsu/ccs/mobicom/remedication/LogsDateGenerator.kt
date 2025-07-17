package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class LogsDateGenerator {
    companion object{
        private val executorService = Executors.newSingleThreadExecutor()

        fun generateLogsDates(context: Context, dates: ArrayList<Date>, callback: (ArrayList<LogsDate>) -> Unit) {
            executorService.execute {
                val logsDates = ArrayList<LogsDate>()
                val myLogDbHelper = LogDbHelper.getInstance(context)
                val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                for ((index, date) in dates.withIndex()){
                    val formattedDate = formatter.format(date)
                    val logList = myLogDbHelper?.getAllLogsDate(date) ?: emptyList()
                    val logsDate = LogsDate(
                        formattedDate,
                        logList as ArrayList<Log>,
                        index == 0
                    )
                    logsDates.add(logsDate)
                }

                Handler(Looper.getMainLooper()).post {
                    callback(logsDates)
                }
            }
        }
    }
}