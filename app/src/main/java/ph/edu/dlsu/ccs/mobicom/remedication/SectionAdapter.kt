package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
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
        markOverdueItems()

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

    fun getPhilippineTime(): Calendar {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
        return calendar
    }

    fun updateSectionBasedOnTime(sections: List<Section>) {
        val currentHour = getPhilippineTime().get(Calendar.HOUR_OF_DAY)

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

    private fun markOverdueItems() {     //Add here for missed
        val periodRank = mapOf(
            "Early Morning"  to 0,
            "Morning"        to 1,
            "Afternoon"      to 2,
            "Night"          to 3
        )

        val currentHour = getPhilippineTime().get(Calendar.HOUR_OF_DAY)
        val currentPeriod = when (currentHour) {
            in 0..5   -> 0
            in 6..11  -> 1
            in 12..17 -> 2
            else      -> 3
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) // Date format (year-month-day)
        val timeFormat = SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()) // Time format (hours:minutes:seconds)

        for (section in sections) {
            val sectionRank = periodRank[section.label] ?: continue
            val isInPast = sectionRank < currentPeriod

            for (item in section.checklist) {
                item.isOverdue = isInPast && !item.isChecked
                if (item.isOverdue){
                    executorService.execute {
                        val currentDate = Date()
                        val formattedDate = dateFormat.format(currentDate)
                        val formattedTime = timeFormat.format(currentDate)

                        val log = Log(
                            formattedDate,   //now
                            formattedTime,   //this time
                            item.medicineName,
                            item.dosage,
                            true
                        )
                        val newId = myDbHelper.insertLog(log)

                        android.util.Log.d("SectionAdapter", "new Log Overdue: ${newId}")
                    }
                }
            }
        }
    }

    fun isAllSectionsEmpty(): Boolean =
        sections.all { it.checklist.isEmpty() }
}