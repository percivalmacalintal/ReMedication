package ph.edu.dlsu.ccs.mobicom.remedication

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    private val sectionList: ArrayList<Section> = SectionDataGenerator.generateData()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        this.recyclerView = findViewById(R.id.sectionRv)
        this.recyclerView.adapter = SectionAdapter(this.sectionList)

        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}