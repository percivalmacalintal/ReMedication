package ph.edu.dlsu.ccs.mobicom.remedication

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import android.provider.Settings
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderReceiver : BroadcastReceiver() {
    private lateinit var contextText: String

    override fun onReceive(context: Context, intent: Intent) {
        val label = intent.getStringExtra("reminder_label") ?: return
        val medicineName = intent.getStringExtra("medicine_name")
        if (medicineName != null) {
            contextText = "Your medicine $medicineName is about to run out. It's time to refill."
        } else {
            contextText = "It's time to take your medication."
        }
        Log.d("ReminderReceiver", "Alarm fired for reminder.")

        // Show notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setContentTitle("Reminder: $label")
            .setContentText(contextText)
            .setSmallIcon(R.drawable.remedication_icon)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(label.hashCode(), notification)

        // Reschedule the same reminder for the next day
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val timeStr = sharedPreferences.getString(label, null) ?: return
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        try {
            val parsedTime = formatter.parse(timeStr)
            val parsedCalendar = Calendar.getInstance().apply {
                time = parsedTime!!
            }

            val now = Calendar.getInstance()
            val nextAlarm = Calendar.getInstance().apply {
                set(Calendar.YEAR, now.get(Calendar.YEAR))
                set(Calendar.MONTH, now.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 1)

                set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val newIntent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("reminder_label", label)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                label.hashCode(),
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                    return
                }
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarm.timeInMillis,
                pendingIntent
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}
