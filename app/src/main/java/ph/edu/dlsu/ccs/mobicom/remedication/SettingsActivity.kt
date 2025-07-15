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
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.provider.Settings
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class SettingsActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private val settingsList: ArrayList<Setting> = SettingsDataGenerator.generateData()
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE)

        this.recyclerView = findViewById(R.id.settingsRv)
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        this.recyclerView.adapter = SettingsAdapter(this.settingsList) {
            scheduleAllDailyReminders()
        }

        notificationSwitch = findViewById(R.id.notificationSw)

        val isNotificationEnabled = sharedPreferences.getBoolean("notification_enabled", false)
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationSwitch.isChecked = false
            disableNotifications()
        } else {
            notificationSwitch.isChecked = isNotificationEnabled
            if (isNotificationEnabled) {
                enableNotifications()
            } else {
                disableNotifications()
            }
        }

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("notification_enabled", isChecked)
            editor.apply()

            if (isChecked) {
                // Check if the permission is granted
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    enableNotifications()
                } else {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                disableNotifications()
            }
        }

        val resetBtn = findViewById<Button>(R.id.resetBtn)
        resetBtn.setOnClickListener {
            recyclerView.adapter?.notifyDataSetChanged()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.setsIt
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeIt -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.medsIt -> {
                    val intent = Intent(this, MedicineActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.logsIt -> {
                    val intent = Intent(this, LogsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.setsIt -> true
                else -> false
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.setsIt
    }
    private fun enableNotifications() {
        Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show()
        createNotificationChannel()
        scheduleAllDailyReminders()
    }
    private fun disableNotifications() {
        Toast.makeText(this, "Notifications disabled!", Toast.LENGTH_SHORT).show()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
        cancelAllScheduledReminders(this)
    }
    private fun scheduleAllDailyReminders() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                this@SettingsActivity.startActivity(intent)
                Toast.makeText(this@SettingsActivity, "Please allow exact alarm permission", Toast.LENGTH_LONG).show()
                return
            }
        }

        cancelAllScheduledReminders(this)

        val timesMap = mapOf(
            "Early Morning" to "02:00 AM",
            "Morning" to "08:00 AM",
            "Afternoon" to "02:00 PM",
            "Night" to "08:00 PM"
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
                    // Set today's date
                    set(Calendar.YEAR, now.get(Calendar.YEAR))
                    set(Calendar.MONTH, now.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

                    // Set time from parsed reminder
                    set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // Schedule for tomorrow if already passed
                    if (before(now)) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                val intent = Intent(this, ReminderReceiver::class.java).apply {
                    putExtra("reminder_label", label)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
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




    //private fun scheduleAllDailyReminders() {
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    //        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    //        if (!alarmManager.canScheduleExactAlarms()) {
    //            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    //            startActivity(intent)
    //            Toast.makeText(this, "Please allow exact alarm permission", Toast.LENGTH_LONG).show()
    //            return
    //        }
    //    }
    //
    //
    //    val timesMap = mapOf(
    //        "Early Morning" to "02:00 AM",
    //        "Morning" to "08:00 AM",
    //        "Afternoon" to "02:00 PM",
    //        "Night" to "08:00 PM"
    //    )
    //
    //    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    //
    //    for ((label, _) in timesMap) {
    //        // Instead of parsing stored time, we just trigger after 10 seconds
    //        val calendar = Calendar.getInstance().apply {
    //            add(Calendar.SECOND, 10) // Trigger after 10 seconds for testing
    //        }
    //
    //        val intent = Intent(this, ReminderReceiver::class.java)
    //        val pendingIntent = PendingIntent.getBroadcast(
    //            this,
    //            label.hashCode(),
    //            intent,
    //            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    //        )
    //
    //        alarmManager.setExactAndAllowWhileIdle(
    //            AlarmManager.RTC_WAKEUP,
    //            calendar.timeInMillis,
    //            pendingIntent
    //        )
    //    }
    //}

    private fun cancelAllScheduledReminders(context: Context) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val keys = listOf("Early Morning", "Morning", "Afternoon", "Night")

        for (key in keys) {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("reminder_label", key) // ✅ must match the original intent
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminder_channel"
            val channelName = "Medication Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for daily medication reminders"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}