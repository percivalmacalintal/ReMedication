package ph.edu.dlsu.ccs.mobicom.remedication

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderManager {

    fun enableNotifications(context: Context) {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        Toast.makeText(context, "Notifications enabled!", Toast.LENGTH_SHORT).show()
        createNotificationChannel(context)
        scheduleAllDailyReminders(context, sharedPreferences)
        scheduleReminderForRefill(context, sharedPreferences)
    }

    fun disableNotifications(context: Context) {
        Toast.makeText(context, "Notifications disabled!", Toast.LENGTH_SHORT).show()
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
        cancelAllScheduledDailyReminders(context)
        cancelRemindersForRefill(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminder_channel"
            val channelName = "Medication Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for daily medication reminders"
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleAllDailyReminders(context: Context, sharedPreferences: SharedPreferences) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                Toast.makeText(context, "Please allow exact alarm permission", Toast.LENGTH_LONG).show()
                return
            }
        }

        cancelAllScheduledDailyReminders(context)

        val timesMap = mapOf(
            "Early Morning" to "02:00 AM",
            "Morning" to "08:00 AM",
            "Afternoon" to "02:00 PM",
            "Night" to "08:00 PM"
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        for ((label, defaultTime) in timesMap) {
            val timeStr = sharedPreferences.getString(label, defaultTime) ?: defaultTime
            try {
                val parsedTime = formatter.parse(timeStr)
                val parsedCalendar = Calendar.getInstance().apply {
                    time = parsedTime!!
                }

                val now = Calendar.getInstance()
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, now.get(Calendar.YEAR))
                    set(Calendar.MONTH, now.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    if (before(now)) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("reminder_label", label)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    label.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    fun scheduleReminderForRefill(context: Context, sharedPreferences: SharedPreferences) {
        val dbHelper = MedicineDbHelper.getInstance(context)
        val medicines = dbHelper?.getAllMedicinesDefault() ?: return

        cancelRemindersForRefill(context)

        val refillReminderTime = "08:00 AM"

        for (m in medicines) {
            val frequencyNumber = when (m.frequency) {
                "Once a day" -> 1
                "Twice a day" -> 2
                "Thrice a day" -> 3
                else -> 0
            }

            val daysLeftBeforeReminder = when (sharedPreferences.getString("Days Left Before Refill", "3 Days")) {
                "1 Day" -> 1
                "2 Days" -> 2
                "3 Days" -> 3
                "4 Days" -> 4
                "5 Days" -> 5
                "6 Days" -> 6
                "7 Days" -> 7
                else -> 0
            }

            val daysLeftBeforeMedicineRunsOut = m.remaining / frequencyNumber

            if (daysLeftBeforeMedicineRunsOut <= daysLeftBeforeReminder) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                try {
                    val parsedTime = formatter.parse(refillReminderTime)
                    val parsedCalendar = Calendar.getInstance().apply {
                        time = parsedTime!!
                    }

                    val now = Calendar.getInstance()
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, now.get(Calendar.YEAR))
                        set(Calendar.MONTH, now.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                        set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                        set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)

                        if (before(now)) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

                    val intent = Intent(context, ReminderReceiver::class.java).apply {
                        putExtra("reminder_label", "Refill Reminder for ${m.name}")
                        putExtra("medicine_name", m.name)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        m.id.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun cancelAllScheduledDailyReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val keys = listOf("Early Morning", "Morning", "Afternoon", "Night")

        for (key in keys) {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("reminder_label", key)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                key.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    fun cancelRemindersForRefill(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dbHelper = MedicineDbHelper.getInstance(context)
        val medicines = dbHelper?.getAllMedicinesDefault() ?: return

        for (m in medicines) {
            val reminderLabel = "Refill Reminder for ${m.name}"
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("reminder_label", reminderLabel)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                m.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }
}