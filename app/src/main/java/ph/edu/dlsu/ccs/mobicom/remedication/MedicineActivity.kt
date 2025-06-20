package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MedicineActivity : ComponentActivity() {

    private val medicineList : ArrayList<Medicine> = MedicineGenerator.generateData()

    private lateinit var recyclerView: RecyclerView
    private lateinit var medAdapter: MedicineAdapter

    private val infoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val position = result.data?.getIntExtra(InfoActivity.POSITION_KEY, -1)

            if (position != -1) { // If position is returned, it indicates an update or deletion
                // Check if the result contains the updated data
                val updatedName = result.data!!.getStringExtra(InfoActivity.NAME_KEY)
                val updatedDosage = result.data!!.getIntExtra(InfoActivity.DOSAGE_KEY, 0)
                val updatedUnit = result.data!!.getStringExtra(InfoActivity.UNIT_KEY) ?: ""
                val updatedFrequency = result.data!!.getStringExtra(InfoActivity.FREQUENCY_KEY) ?: ""
                val updatedRemaining = result.data!!.getIntExtra(InfoActivity.REMAINING_KEY, 0)
                val updatedStartDate = result.data!!.getStringExtra(InfoActivity.START_KEY) ?: ""
                val updatedEndDate = result.data!!.getStringExtra(InfoActivity.END_KEY) ?: ""
                if (updatedName != null) {
                    medicineList[position!!] = Medicine(android.R.drawable.ic_menu_report_image, updatedName, updatedDosage, updatedUnit, updatedFrequency, updatedRemaining, updatedStartDate, updatedEndDate)
                    medAdapter.notifyItemChanged(position)
                } else {
                    if (position != null) {
                        medicineList.removeAt(position)
                        medAdapter.notifyItemRemoved(position)
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine)

        this.recyclerView = findViewById(R.id.medicineRv)
        this.medAdapter = MedicineAdapter(this.medicineList, infoResultLauncher)
        this.recyclerView.adapter = medAdapter

        this.recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val spacing = (32 * Resources.getSystem().displayMetrics.density).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

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
}