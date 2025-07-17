package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.concurrent.Executors
import java.util.Date


class LogsActivity : ComponentActivity() {
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var LogsDateList : ArrayList<LogsDate>
    private lateinit var MedicineList : ArrayList<Medicine>
    private lateinit var myMedicineDbHelper: MedicineDbHelper
    private lateinit var dates: ArrayList<Date>

    private lateinit var monthSp: Spinner
    private lateinit var daySp: Spinner
    private lateinit var yearSp: Spinner
    private lateinit var medicineSp: Spinner

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        dates = ArrayList()
        val calendar = Calendar.getInstance()
        for (i in 0..4) {
            calendar.time = Date() // Reset to the current date
            calendar.add(Calendar.DAY_OF_YEAR, -i) // Subtract i days from the current date
            dates.add(calendar.time) // Add the date to the list
        }

        this.recyclerView = findViewById(R.id.logsRv)

        LogsDateGenerator.generateLogsDates(this, dates) { logsDates ->
            LogsDateList = logsDates

            this.recyclerView.adapter = LogsDateAdapter(this.LogsDateList)

            this.recyclerView.layoutManager = LinearLayoutManager(this)
        }

        //  set up spinners
        this.monthSp = findViewById(R.id.monthSp)
        this.daySp = findViewById(R.id.daySp)
        this.yearSp = findViewById(R.id.yearSp)
        this.medicineSp = findViewById(R.id.medicineSp)

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
            MedicineList = myMedicineDbHelper.getAllMedicinesDefault()
            val medicines = mutableListOf("Medicine Name")
            for (medicine in MedicineList) {
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
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.logsIt
    }

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

    // Get the max days in a month, accounting for leap years
    private fun getMaxDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            else -> 31
        }
    }
}