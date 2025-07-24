package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.concurrent.Executors
import java.util.Date

class LogsActivity : ComponentActivity() {
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var logsDateList : ArrayList<LogsDate>
    private lateinit var medicineList : ArrayList<Medicine>
    private lateinit var myMedicineDbHelper: MedicineDbHelper
    private lateinit var dates: ArrayList<Date>

    private lateinit var monthSp: Spinner
    private lateinit var daySp: Spinner
    private lateinit var yearSp: Spinner
    private lateinit var medicineSp: Spinner

    private lateinit var searchBtn: Button

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        this.recyclerView = findViewById(R.id.logsRv)
        this.searchBtn = findViewById(R.id.searchBtn)
        //  spinners
        this.monthSp = findViewById(R.id.monthSp)
        this.daySp = findViewById(R.id.daySp)
        this.yearSp = findViewById(R.id.yearSp)
        this.medicineSp = findViewById(R.id.medicineSp)

        //  Set up Logs
        dates = ArrayList()
        val calendar = Calendar.getInstance()
        for (i in 0..4) {
            calendar.time = Date() // Reset to the current date
            calendar.add(Calendar.DAY_OF_YEAR, -i) // Subtract i days from the current date
            dates.add(calendar.time) // Add the date to the list
        }

        LogsDateGenerator.generateLogsDates(this, dates) { logsDates ->
            logsDateList = logsDates

            printLogsToLog()

            this.recyclerView.adapter = LogsDateAdapter(this.logsDateList)

            this.recyclerView.layoutManager = LinearLayoutManager(this)
        }

        //  Set up spinners
        val months = listOf("Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSp.adapter = monthAdapter

        //  day
        val days = mutableListOf("Day")
        for (i in 1..31) {
            days.add(String.format("%02d", i))
        }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySp.adapter = dayAdapter

        //  year
        val years = mutableListOf("Year")
        for (i in 2025..2100) {
            years.add(i.toString())
        }
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSp.adapter = yearAdapter

        //  medicine
        executorService.execute {
            myMedicineDbHelper = MedicineDbHelper.getInstance(this@LogsActivity)!!
            medicineList = myMedicineDbHelper.getAllMedicinesDefault()
            val medicines = mutableListOf("Medicine Name")
            for (medicine in medicineList) {
                medicines.add(medicine.name)
            }
            val medicineAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicines)
            medicineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            medicineSp.adapter = medicineAdapter
        }

        monthSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                updateDaySpinner()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        yearSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                updateDaySpinner()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.logsIt
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
                R.id.logsIt -> true
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

        searchBtn.setOnClickListener {
            val selectedYear = yearSp.selectedItem.toString()
            val selectedMonth = monthSp.selectedItem.toString()
            val selectedDay = daySp.selectedItem.toString()
            val selectedMedicine = medicineSp.selectedItem.toString()

            // Check if the any spinner was used
            if (selectedYear == "Year" && selectedMonth == "Month" && selectedDay == "Day" &&
                selectedMedicine == "Medicine Name") {
                Toast.makeText(this, "Please select at least one filter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Fetch filtered logs from the database based on the spinner selections
            executorService.execute {
                //  Set up filter values
                val queryYear = if (selectedYear == "Year") "%" else selectedYear
                val queryMonth = if (selectedMonth == "Month") "%" else monthNameToNumber(selectedMonth)
                val queryDay = if (selectedDay == "Day") "%" else selectedDay
                val queryMedicine = if (selectedMedicine == "Medicine Name") "%" else selectedMedicine
                // Fetch data from the database based on filters
                LogsDateGenerator.generateSearchLogsDates(this, queryYear, queryMonth, queryDay, queryMedicine) { logsDates ->
                    logsDateList = logsDates

                    printLogsToLog()

                    this.recyclerView.adapter = LogsDateAdapter(this.logsDateList)

                    this.recyclerView.layoutManager = LinearLayoutManager(this)
                }
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.logsIt
    }
    // change updateDaySpinner to only reset if the currently selected day would not be valid in that month/year
    private fun updateDaySpinner() {
        val selectedMonth = if (monthSp.selectedItemPosition == 0) {
            1
        } else {
            monthSp.selectedItemPosition
        }
        val selectedYear = if (yearSp.selectedItemPosition == 0) {
            2025
        } else {
            yearSp.selectedItem.toString().toInt()
        }
        val maxDays = getMaxDaysInMonth(selectedMonth, selectedYear)

        val days = mutableListOf("Day")
        for (i in 1..maxDays) {
            days.add(String.format("%02d", i))
        }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySp.adapter = dayAdapter
    }

    private fun getMaxDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            else -> 31
        }
    }

    private fun monthNameToNumber(monthName: String): String {
        // A map of month names to their corresponding numbers (01 to 12)
        val monthMap = mapOf(
            "January" to "01",
            "February" to "02",
            "March" to "03",
            "April" to "04",
            "May" to "05",
            "June" to "06",
            "July" to "07",
            "August" to "08",
            "September" to "09",
            "October" to "10",
            "November" to "11",
            "December" to "12"
        )

        // Return the corresponding month number or an empty string if the month name is invalid
        return monthMap[monthName] ?: ""
    }

    private fun printLogsToLog() {
        for (ld in logsDateList) {
            android.util.Log.d("LogsActivity", "printLogDate: ${ld.logs}")
            for(log in ld.logs){
                android.util.Log.d("LogsActivity", "printAllLogs: ${log.name}")
            }
        }
    }
}