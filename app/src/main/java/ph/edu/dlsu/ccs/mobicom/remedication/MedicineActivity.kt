package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityMedicineBinding
import java.util.concurrent.Executors

class MedicineActivity : ComponentActivity() {
    companion object {
        const val RESULT_EDIT = 200
        const val RESULT_DELETE = 300
    }

    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var medicines : ArrayList<Medicine>
    private lateinit var medAdapter: MedicineAdapter
    private lateinit var myDbHelper: MedicineDbHelper

    private val infoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_EDIT) {
//            val position = result.data?.getIntExtra(InfoActivity.POSITION_KEY, -1)
            val id = result.data!!.getLongExtra(InfoActivity.ID_KEY, -1)
            val index = medicines.indexOfFirst { it.id == id }

            if (index != -1) {
                val updatedImage = result.data!!.getStringExtra(InfoActivity.IMAGE_KEY) ?: ""
                val updatedName = result.data!!.getStringExtra(InfoActivity.NAME_KEY) ?: ""
                val updatedDosage = result.data!!.getIntExtra(InfoActivity.DOSAGE_KEY, 0)
                val updatedUnit = result.data!!.getStringExtra(InfoActivity.UNIT_KEY) ?: ""
                val updatedFrequency = result.data!!.getStringExtra(InfoActivity.FREQUENCY_KEY) ?: ""
                val updatedTimeOfDay = result.data!!.getIntegerArrayListExtra(InfoActivity.TIMEOFDAY_KEY) ?: listOf()
                val updatedRemaining = result.data!!.getIntExtra(InfoActivity.REMAINING_KEY, 0)
                val updatedStartDate = result.data!!.getStringExtra(InfoActivity.START_KEY) ?: ""
                val updatedEndDate = result.data!!.getStringExtra(InfoActivity.END_KEY) ?: ""
                medicines[index] = Medicine(id, updatedImage, updatedName, updatedDosage, updatedUnit, updatedFrequency, updatedTimeOfDay, updatedRemaining, updatedStartDate, updatedEndDate)
                medAdapter.notifyItemChanged(index)
                printMedicinesToLog()
            }
        } else if(result.resultCode == RESULT_DELETE){
            val position = result.data!!.getIntExtra(InfoActivity.POSITION_KEY, -1)
            medicines.removeAt(position)
            medAdapter.notifyItemRemoved(position)

            printMedicinesToLog()
        }
    }

    private val newMedicineResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val id = result.data!!.getLongExtra(NewMedicineActivity.NEW_ID_KEY, -1)
            val image = result.data!!.getStringExtra(NewMedicineActivity.NEW_IMAGE_KEY) ?: ""
            val name = result.data!!.getStringExtra(NewMedicineActivity.NEW_NAME_KEY) ?: ""
            val dosage = result.data!!.getIntExtra(NewMedicineActivity.NEW_DOSAGE_KEY, 0)
            val unit = result.data!!.getStringExtra(NewMedicineActivity.NEW_UNIT_KEY) ?: ""
            val frequency = result.data!!.getStringExtra(NewMedicineActivity.NEW_FREQUENCY_KEY) ?: ""
            val timeOfDay = result.data!!.getIntegerArrayListExtra(NewMedicineActivity.NEW_TIMEOFDAY_KEY) ?: arrayListOf()
            val remaining = result.data!!.getIntExtra(NewMedicineActivity.NEW_REMAINING_KEY, 0)
            val startDate = result.data!!.getStringExtra(NewMedicineActivity.NEW_START_KEY) ?: ""
            val endDate = result.data!!.getStringExtra(NewMedicineActivity.NEW_END_KEY) ?: ""
            medicines.add(Medicine(id, image, name, dosage, unit, frequency, timeOfDay, remaining, startDate, endDate))
            medAdapter.notifyItemInserted(medicines.size - 1)
            printMedicinesToLog()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityMedicineBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        executorService.execute {
            myDbHelper = MedicineDbHelper.getInstance(this@MedicineActivity)!!
            medicines = myDbHelper.getAllMedicinesDefault()

            printMedicinesToLog()

            medAdapter = MedicineAdapter(medicines, infoResultLauncher)
            viewBinding.medicineRv.adapter = medAdapter
            viewBinding.medicineRv.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            val spacing = (32 * Resources.getSystem().displayMetrics.density).toInt()
            viewBinding.medicineRv.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
        }

        viewBinding.addBtn.setOnClickListener {
            val intent = Intent(applicationContext, NewMedicineActivity::class.java)
            newMedicineResultLauncher.launch(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.navBnv)
        bottomNav.selectedItemId = R.id.medsIt
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeIt -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.medsIt -> true
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
        bottomNav.selectedItemId = R.id.medsIt
    }

    private fun printMedicinesToLog() {
        for (m in medicines) {
            Log.d("MedicineActivity", "printAllMedicines: ${m.id}")
        }
    }
}