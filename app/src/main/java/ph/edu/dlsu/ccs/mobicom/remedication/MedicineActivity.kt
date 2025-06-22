package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityMedicineBinding

class MedicineActivity : ComponentActivity() {

    private val medicineList : ArrayList<Medicine> = MedicineGenerator.generateData()

    private lateinit var medAdapter: MedicineAdapter

    private val infoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val position = result.data?.getIntExtra(InfoActivity.POSITION_KEY, -1)

            if (position != -1) {
                val updatedImage = result.data!!.getIntExtra(InfoActivity.IMAGE_KEY, 0)
                val updatedName = result.data!!.getStringExtra(InfoActivity.NAME_KEY)
                val updatedDosage = result.data!!.getIntExtra(InfoActivity.DOSAGE_KEY, 0)
                val updatedUnit = result.data!!.getStringExtra(InfoActivity.UNIT_KEY) ?: ""
                val updatedFrequency = result.data!!.getStringExtra(InfoActivity.FREQUENCY_KEY) ?: ""
                val updatedTimeOfDay = result.data!!.getIntegerArrayListExtra(InfoActivity.TIMEOFDAY_KEY) ?: listOf()
                val updatedRemaining = result.data!!.getIntExtra(InfoActivity.REMAINING_KEY, 0)
                val updatedStartDate = result.data!!.getStringExtra(InfoActivity.START_KEY) ?: ""
                val updatedEndDate = result.data!!.getStringExtra(InfoActivity.END_KEY) ?: ""
                if (updatedName != null) {
                    medicineList[position!!] = Medicine(updatedImage, updatedName, updatedDosage, updatedUnit, updatedFrequency, updatedTimeOfDay, updatedRemaining, updatedStartDate, updatedEndDate)
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

    private val newMedicineResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val image = result.data!!.getIntExtra(NewMedicineActivity.NEW_IMAGE_KEY, 0)
            val name = result.data!!.getStringExtra(NewMedicineActivity.NEW_NAME_KEY) ?: ""
            val dosage = result.data!!.getIntExtra(NewMedicineActivity.NEW_DOSAGE_KEY, 0)
            val unit = result.data!!.getStringExtra(NewMedicineActivity.NEW_UNIT_KEY) ?: ""
            val frequency = result.data!!.getStringExtra(NewMedicineActivity.NEW_FREQUENCY_KEY) ?: ""
            val timeOfDay = result.data!!.getIntegerArrayListExtra(NewMedicineActivity.NEW_TIMEOFDAY_KEY) ?: arrayListOf()
            val remaining = result.data!!.getIntExtra(NewMedicineActivity.NEW_REMAINING_KEY, 0)
            val startDate = result.data!!.getStringExtra(NewMedicineActivity.NEW_START_KEY) ?: ""
            val endDate = result.data!!.getStringExtra(NewMedicineActivity.NEW_END_KEY) ?: ""
            medicineList.add(Medicine(image, name, dosage, unit, frequency, timeOfDay, remaining, startDate, endDate))
            medAdapter.notifyItemInserted(medicineList.size - 1)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityMedicineBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        this.medAdapter = MedicineAdapter(this.medicineList, infoResultLauncher)
        viewBinding.medicineRv.adapter = medAdapter
        viewBinding.medicineRv.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val spacing = (32 * Resources.getSystem().displayMetrics.density).toInt()
        viewBinding.medicineRv.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

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
}