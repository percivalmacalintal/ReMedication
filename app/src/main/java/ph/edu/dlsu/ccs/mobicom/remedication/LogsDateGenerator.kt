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

        fun generateLogsDates(context: Context, callback: (ArrayList<LogsDate>, Boolean) -> Unit) {
            executorService.execute {
                val logsDates = ArrayList<LogsDate>()
                val isSearching = false;
                val myLogDbHelper = LogDbHelper.getInstance(context)
                val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                val logList = myLogDbHelper?.getAllLogsDefault() ?: emptyList()

                val uniqueDates = logList.map { it.date }.toSet()
                for ((index, uniqueDate) in uniqueDates.withIndex()) {
                    val formattedDate = formatter.format(uniqueDate)
                    val logsForThisDate = logList.filter { it.date == uniqueDate }
                    val logsDate = LogsDate(
                        formattedDate,
                        logsForThisDate as ArrayList<Log>,
                        index == 0
                    )
                    logsDates.add(logsDate)
                }

                Handler(Looper.getMainLooper()).post {
                    callback(logsDates, isSearching)
                }
            }
        }

        fun generateSearchLogsDates(context: Context, year: String, month: String, day: String, medicine: String, callback: (ArrayList<LogsDate>, Boolean) -> Unit) {
            executorService.execute {
                val logsDates = ArrayList<LogsDate>()
                val isSearching = true;
                val myLogDbHelper = LogDbHelper.getInstance(context)
                val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                val logList = myLogDbHelper?.getAllLogsSearch(year, month, day, medicine) ?: emptyList()

                val uniqueDates = logList.map { it.date }.toSet()
                for ((index, uniqueDate) in uniqueDates.withIndex()) {
                    val formattedDate = formatter.format(uniqueDate)
                    val logsForThisDate = logList.filter { it.date == uniqueDate }
                    val logsDate = LogsDate(
                        formattedDate,
                        logsForThisDate as ArrayList<Log>,
                        index == 0
                    )
                    logsDates.add(logsDate)
                }
                Handler(Looper.getMainLooper()).post {
                    callback(logsDates, isSearching)
                }
            }
        }
    }
}