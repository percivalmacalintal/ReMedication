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
import java.util.concurrent.Executors


class SettingsActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var medicines : ArrayList<Medicine>
    private lateinit var myDbHelper: MedicineDbHelper
    private val settingsList: ArrayList<Setting> = SettingsDataGenerator.generateData()
    private val executorService = Executors.newSingleThreadExecutor()
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE)

        if (sharedPreferences.getBoolean("notification_enabled", false) &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED)
        ) {
            ReminderManager.scheduleAllDailyReminders(this, sharedPreferences)
            ReminderManager.scheduleReminderForRefill(this, sharedPreferences)
        }

        this.recyclerView = findViewById(R.id.settingsRv)
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        this.recyclerView.adapter = SettingsAdapter(this.settingsList) {
            ReminderManager.scheduleAllDailyReminders(this, sharedPreferences)
            ReminderManager.scheduleReminderForRefill(this, sharedPreferences)
        }

        notificationSwitch = findViewById(R.id.notificationSw)

        val isNotificationEnabled = sharedPreferences.getBoolean("notification_enabled", false)
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationSwitch.isChecked = false
            ReminderManager.disableNotifications(this)
        } else {
            notificationSwitch.isChecked = isNotificationEnabled
            if (isNotificationEnabled) {
                ReminderManager.enableNotifications(this)
            } else {
                ReminderManager.disableNotifications(this)
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
                    ReminderManager.enableNotifications(this)
                } else {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                ReminderManager.disableNotifications(this)
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
}