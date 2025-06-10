package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MedicineActivity : ComponentActivity() {

    private val medicineList : ArrayList<Medicine> = MedicineGenerator.generateData()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine)

        this.recyclerView = findViewById(R.id.medicineRv)

        this.recyclerView.adapter = MedicineAdapter(this.medicineList)

        this.recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val spacing = (32 * Resources.getSystem().displayMetrics.density).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
    }
}