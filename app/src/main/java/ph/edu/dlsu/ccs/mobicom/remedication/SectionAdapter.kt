package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.Executors

class SectionAdapter(private val sections: List<Section>) :
    RecyclerView.Adapter<SectionViewHolder>() {

    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var myDbHelper: LogDbHelper

//    init {
//        updateSectionBasedOnTime(sections)
//        markOverdueItems()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_layout, parent, false)

        myDbHelper = LogDbHelper.getInstance(parent.context)!!

        updateSectionBasedOnTime(sections)
        markOverdueItems(parent.context)

        return SectionViewHolder(view)  // Using SectionViewHolder from the other file
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bindData(section)  // Use bindData method from SectionViewHolder

        val adapter = ChecklistAdapter(section.checklist)
        holder.checklistRv.adapter = adapter

        holder.itemView.setOnClickListener {
            section.isExpanded = !section.isExpanded
            notifyItemChanged(position)
        }
        holder.dropdownBtn.setOnClickListener {
            section.isExpanded = !section.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = sections.size

    fun getLocalTime(): Calendar {
        val calendar = Calendar.getInstance()
        return calendar
    }

    fun updateSectionBasedOnTime(sections: List<Section>) {
        val currentHour = getLocalTime().get(Calendar.HOUR_OF_DAY)

        for (section in sections) {
            section.isExpanded = when (section.label) {
                "Early Morning" -> currentHour in 0..5
                "Morning"        -> currentHour in 6..11
                "Afternoon"      -> currentHour in 12..17
                "Night"          -> currentHour in 18..23
                else             -> false
            }
        }
    }

    private fun markOverdueItems(context: Context?) {
        val periodRank = mapOf(
            "Early Morning"  to 0,
            "Morning"        to 1,
            "Afternoon"      to 2,
            "Night"          to 3
        )

        val currentHour = getLocalTime().get(Calendar.HOUR_OF_DAY)
        val currentPeriod = when (currentHour + 6) {  // Shift current hour by +6 if needed
            in 0..5   -> 0
            in 6..11  -> 1
            in 12..17 -> 2
            else      -> 3
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())

        for ((sectionIndex, section) in sections.withIndex()) {
            val sectionRank = periodRank[section.label] ?: continue
            val isInPast = sectionRank < currentPeriod

            // Loop through items in the checklist
            for ((itemIndex, item) in section.checklist.withIndex()) {
                item.isOverdue = isInPast && !item.isChecked

                // Check if a log has already been created for this item
                if (item.isOverdue && !getIsLogCreated(item.id, context)) {
                    // Log has not been created, create it
                    android.util.Log.d("SectionAdapter", "Creating log for item: ${item.medicineName}, isLogCreated: ${item.isLogCreated} from section: ${section.label}")

                    executorService.execute {
                        val currentDate = Date()
                        val formattedDate = dateFormat.format(currentDate)
                        val formattedTime = timeFormat.format(currentDate)

                        // Create the log object
                        val log = Log(
                            formattedDate,
                            formattedTime,
                            item.medicineName,
                            item.dosage,
                            true  // Mark it as overdue
                        )

                        // Insert the log into the database and get the new ID
                        val newId = myDbHelper.insertLog(log)

                        // Log to confirm database insertion
                        android.util.Log.d("SectionAdapter", "Log inserted for ${item.medicineName}, New ID: $newId")

                        // Save the log creation flag to SharedPreferences
                        saveIsLogCreated(item.id, true, context)

                        // Post updates to the main thread
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            // After inserting the log, update the item state
                            item.logID = newId

                            // Debug: Log the state after update
                            android.util.Log.d("SectionAdapter", "isLogCreated set to true for item: ${item.medicineName}, logID: $newId")

                            // Notify the RecyclerView about the change in the item
                            val globalPosition = calculateGlobalPosition(sectionIndex, itemIndex)
                            notifyItemChanged(globalPosition)

                            // Debug: Final log after update
                            android.util.Log.d("SectionAdapter", "New Log Overdue: $newId")
                        }
                    }
                }
            }
        }
    }

    fun saveIsLogCreated(itemId: Long, isCreated: Boolean, context: Context) {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isLogCreated_$itemId", isCreated)
        editor.apply()  // Commit the changes asynchronously
    }

    fun getIsLogCreated(itemId: Long, context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isLogCreated_$itemId", false)  // Default is false if not found
    }

    private fun calculateGlobalPosition(sectionIndex: Int, itemIndex: Int): Int {
        var globalPosition = 0
        for (i in 0 until sectionIndex) {
            globalPosition += sections[i].checklist.size
        }
        globalPosition += itemIndex

        return globalPosition
    }

    fun isAllSectionsEmpty(): Boolean =
        sections.all { it.checklist.isEmpty() }

}

