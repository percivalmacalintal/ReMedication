package ph.edu.dlsu.ccs.mobicom.remedication

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    // Our data
    private val checklistList: ArrayList<Checklist> = ChecklistDataGenerator.generateData()
    // Our RecyclerView reference
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the RecyclerView
        this.recyclerView = findViewById(R.id.recyclerView)

        // Set the Adapter. We have to define our own Adapter so that we can properly set the
        // information into the item layout we created. It is typical to pass the data we want
        // displayed into the adapter. There are other variants of RecyclerViews that query data
        // from online sources in batches (instead of passing everything), but we'll get to that
        // when we reach accessing remote DBs.
        this.recyclerView.adapter = ChecklistAdapter(this.checklistList)

        // Set the LayoutManager. This can be set to different kinds of LayoutManagers but we're
        // keeping things simple with a LinearLayout.
        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}