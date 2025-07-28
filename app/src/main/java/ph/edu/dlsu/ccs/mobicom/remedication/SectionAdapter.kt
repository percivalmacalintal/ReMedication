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
    val alreadyLogged = mutableSetOf<Long>()

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

    fun markOverdueItems(context: Context) {
        val periodRank = mapOf(
            "Early Morning"  to 0,
            "Morning"        to 1,
            "Afternoon"      to 2,
            "Night"          to 3
        )

        val currentHour = getLocalTime().get(Calendar.HOUR_OF_DAY)
        val currentPeriod = when (currentHour) {
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

            for ((itemIndex, item) in section.checklist.withIndex()) {
                item.isOverdue = isInPast && !item.isChecked
                val isAlreadyMarked = getIsLogCreated(item.id, context) || alreadyLogged.contains(item.id)
                if (item.isOverdue && !isAlreadyMarked) {
                    alreadyLogged.add(item.id)
                    android.util.Log.d("SectionAdapter", "Creating log for Checklist ID: ${item.id} from section: ${section.label}")
                    executorService.execute {
                        val currentDate = Date()
                        val formattedDate = dateFormat.format(currentDate)
                        val formattedTime = timeFormat.format(currentDate)
                        val log = Log(
                            formattedDate,
                            formattedTime,
                            item.medicineName,
                            item.dosage,
                            LogStatus.MISSED
                        )
                        val newId = myDbHelper.insertLog(log)
                        saveNewLog(item.id, newId, true, context)
                        android.util.Log.d("SectionAdapter", "Log inserted for Checklist ID: ${item.id}, New ID: $newId")
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            val globalPosition = calculateGlobalPosition(sectionIndex, itemIndex)
                            notifyItemChanged(globalPosition)
                            android.util.Log.d("SectionAdapter", "UI updated with log ID $newId for item ${item.id}")
                        }
                    }
                }
            }
        }
    }

    fun saveNewLog(itemId: Long, logId: Long, isCreated: Boolean, context: Context) {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("logId_$itemId", logId)
        editor.putBoolean("isLogCreated_$itemId", isCreated)
        editor.apply()
    }

    fun getIsLogCreated(itemId: Long, context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isLogCreated_$itemId", false)
    }

    private fun calculateGlobalPosition(sectionIndex: Int, itemIndex: Int): Int {
        var position = 0
        for (i in 0 until sectionIndex) {
            position += sections[i].checklist.size
        }
        return position + itemIndex
    }

    fun isAllSectionsEmpty(): Boolean =
        sections.all { it.checklist.isEmpty() }

}

