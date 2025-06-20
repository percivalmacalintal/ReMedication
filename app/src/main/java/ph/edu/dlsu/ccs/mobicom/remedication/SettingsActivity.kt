package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.homeIt
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
                R.id.setsIt -> {
                    //
                    true
                }
                else -> false
            }
        }

        val logoIv = findViewById<ImageView>(R.id.remedicationLogoIv)
        logoIv.setImageResource(R.drawable.remedication_logo)

        // Define the time options with 1-hour intervals, from 12 AM to 11 PM
        val afterMidnightSp = findViewById<Spinner>(R.id.afterMidnightSp)
        val morningSp = findViewById<Spinner>(R.id.morningSp)
        val afternoonSp = findViewById<Spinner>(R.id.afternoonSp)
        val nightSp = findViewById<Spinner>(R.id.nightSp)
        val daysLeftSp = findViewById<Spinner>(R.id.daysLeftSp)

        // Define the time options for each spinner (1-hour intervals for each time range)

        val afterMidnightTimes = arrayOf(
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM"
        )

        val morningTimes = arrayOf(
            "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM"
        )

        val afternoonTimes = arrayOf(
            "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"
        )

        val nightTimes = arrayOf(
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        )

        val daysLeft = arrayOf(
            "1 Day", "2 Days", "3 Days", "4 Days", "5 Days", "6 Days", "7 Days"
        )

        // Set adapter to each spinner
        val afterMidnightAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, afterMidnightTimes)
        val morningAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, morningTimes)
        val afternoonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, afternoonTimes)
        val nightAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nightTimes)
        val daysLeftAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysLeft)

        // Set drop-down style
        afterMidnightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        morningAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        afternoonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        nightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daysLeftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter for the spinners
        afterMidnightSp.adapter = afterMidnightAdapter
        morningSp.adapter = morningAdapter
        afternoonSp.adapter = afternoonAdapter
        nightSp.adapter = nightAdapter
        daysLeftSp.adapter = daysLeftAdapter

        // Example of how to handle spinner selection
//        afterMidnightSp.setOnItemSelectedListener { parent, view, position, id ->
//            val selectedItem = parent.getItemAtPosition(position) as String
//            Toast.makeText(this, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
//        }
    }
}