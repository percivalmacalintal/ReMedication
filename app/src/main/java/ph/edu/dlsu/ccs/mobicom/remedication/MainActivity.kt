package ph.edu.dlsu.ccs.mobicom.remedication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : ComponentActivity() {
    private lateinit var sectionList: ArrayList<Section>
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        this.recyclerView = findViewById(R.id.sectionRv)
        this.emptyTv = findViewById(R.id.emptyTv)

        val sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE)
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

        SectionDataGenerator.generateData(this) { sections ->
            Handler(Looper.getMainLooper()).post {
                sectionList = sections

                val adapter = SectionAdapter(sectionList)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)

                showOrHideEmptyMessage(adapter)
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.homeIt
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeIt -> true
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
                R.id.setsIt -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.homeIt  // Or whatever your home item ID is
    }
    private fun showOrHideEmptyMessage(adapter: SectionAdapter) {
        if (adapter.isAllSectionsEmpty()) {
            recyclerView.visibility = View.GONE
            emptyTv.visibility      = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyTv.visibility      = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        SectionDataGenerator.generateData(this) { sections ->
            sectionList = sections

            this.recyclerView.adapter = SectionAdapter(this.sectionList)

            showOrHideEmptyMessage(SectionAdapter(this.sectionList))

            this.recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }
}