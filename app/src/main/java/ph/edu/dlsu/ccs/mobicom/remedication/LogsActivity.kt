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

class LogsActivity : ComponentActivity() {
    private val executorService = Executors.newSingleThreadExecutor()
    private var isSearching = false
    private lateinit var logsDateList : ArrayList<LogsDate>
//    private lateinit var medicineList : ArrayList<Medicine>
//    private lateinit var myMedicineDbHelper: MedicineDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var monthSp: Spinner
    private lateinit var daySp: Spinner
    private lateinit var yearSp: Spinner
    private lateinit var medicineSp: Spinner
    private lateinit var searchBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        this.recyclerView = findViewById(R.id.logsRv)
        this.monthSp = findViewById(R.id.monthSp)
        this.daySp = findViewById(R.id.daySp)
        this.yearSp = findViewById(R.id.yearSp)
        this.medicineSp = findViewById(R.id.medicineSp)
        this.searchBtn = findViewById(R.id.searchBtn)

        LogsDateGenerator.generateLogsDates(this) { logsDates, isSearching ->
            logsDateList = logsDates
            this.isSearching = isSearching

            val uniqueYears = getUniqueYearsFromLogs(logsDateList)
            val uniqueMedicines = getUniqueMedicinesFromLogs(logsDateList)

            setupYearSpinner(uniqueYears)
            setupMedicineSpinner(uniqueMedicines)

            this.recyclerView.adapter = LogsDateAdapter(this.logsDateList)
            this.recyclerView.layoutManager = LinearLayoutManager(this)

            printLogsToLog()
        }

        val months = listOf("Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSp.adapter = monthAdapter

        val days = mutableListOf("Day")
        for (i in 1..31) {
            days.add(String.format("%02d", i))
        }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySp.adapter = dayAdapter

//        val years = mutableListOf("Year")
//        for (i in 2025..2100) {
//            years.add(i.toString())
//        }
//        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
//        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        yearSp.adapter = yearAdapter
//
//        executorService.execute {
//            myMedicineDbHelper = MedicineDbHelper.getInstance(this@LogsActivity)!!
//            medicineList = myMedicineDbHelper.getAllMedicinesDefault()
//            val medicines = mutableListOf("Medicine Name")
//            for (medicine in medicineList) {
//                medicines.add(medicine.name)
//            }
//            val medicineAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicines)
//            medicineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            medicineSp.adapter = medicineAdapter
//        }

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

            if (selectedYear == "Year" && selectedMonth == "Month" && selectedDay == "Day" &&
                selectedMedicine == "Medicine Name") {
                if(isSearching){
                    LogsDateGenerator.generateLogsDates(this) { logsDates, isSearching ->
                        logsDateList = logsDates
                        this.isSearching = isSearching

                        this.recyclerView.adapter = LogsDateAdapter(this.logsDateList)
                        this.recyclerView.layoutManager = LinearLayoutManager(this)

                        printLogsToLog()
                    }
                } else{
                    Toast.makeText(this, "Please select at least one filter", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                executorService.execute {
                    val queryYear = if (selectedYear == "Year") "%" else selectedYear
                    val queryMonth = if (selectedMonth == "Month") "%" else monthNameToNumber(selectedMonth)
                    val queryDay = if (selectedDay == "Day") "%" else selectedDay
                    val queryMedicine = if (selectedMedicine == "Medicine Name") "%" else selectedMedicine
                    LogsDateGenerator.generateSearchLogsDates(this, queryYear, queryMonth, queryDay, queryMedicine) { logsDates, isSearching ->
                        logsDateList = logsDates
                        this.isSearching = isSearching

                        this.recyclerView.adapter = LogsDateAdapter(this.logsDateList)
                        this.recyclerView.layoutManager = LinearLayoutManager(this)

                        printLogsToLog()
                    }
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

    private fun getUniqueYearsFromLogs(logsDateList: ArrayList<LogsDate>): List<String> {
        val years = mutableSetOf<String>()
        for (logDate in logsDateList) {
            val calendar = Calendar.getInstance()
            calendar.time = logDate.date
            val year = calendar.get(Calendar.YEAR).toString()
            years.add(year)
        }
        return listOf("Year") + years.toList()
    }

    private fun getUniqueMedicinesFromLogs(logsDateList: ArrayList<LogsDate>): List<String> {
        val medicines = mutableSetOf<String>()
        for (logDate in logsDateList) {
            for (log in logDate.logs) {
                val medicineName = log.name
                medicines.add(medicineName)
            }
        }
        return listOf("Medicine Name") + medicines.toList()
    }

    private fun setupYearSpinner(years: List<String>) {
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSp.adapter = yearAdapter
    }

    private fun setupMedicineSpinner(medicines: List<String>) {
        val medicineAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicines)
        medicineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        medicineSp.adapter = medicineAdapter
    }

    private fun updateDaySpinner() {
        val selectedMonth = if (monthSp.selectedItemPosition == 0) {
            1
        } else {
            monthSp.selectedItemPosition
        }
        val selectedYear = if (yearSp.selectedItem == null || yearSp.selectedItem.toString() == "Year") {
            2025
        } else {
            yearSp.selectedItem.toString().toInt()
        }
        val maxDays = getMaxDaysInMonth(selectedMonth, selectedYear)
        val currentSelectedDay = daySp.selectedItem.toString().toIntOrNull()
        val days = mutableListOf("Day")
        for (i in 1..maxDays) {
            days.add(String.format("%02d", i))
        }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySp.adapter = dayAdapter
        if (currentSelectedDay == null || currentSelectedDay > maxDays) {
            daySp.setSelection(0)
        } else {
            val dayPosition = days.indexOf(String.format("%02d", currentSelectedDay))
            if (dayPosition != -1) {
                daySp.setSelection(dayPosition)
            }
        }
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