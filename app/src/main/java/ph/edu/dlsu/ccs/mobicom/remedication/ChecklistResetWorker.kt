package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ChecklistResetWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val context = applicationContext
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        sharedPreferences.all.keys
            .filter { it.startsWith("checklist_") }
            .forEach { editor.remove(it) }
        editor.apply()

        return Result.success()
    }
}