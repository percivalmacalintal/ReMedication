package ph.edu.dlsu.ccs.mobicom.remedication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.TimeZone

class SectionAdapter(private val sections: List<Section>) :
    RecyclerView.Adapter<SectionViewHolder>() {

    init {
        updateSectionBasedOnTime(sections)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_layout, parent, false)
        return SectionViewHolder(view)  // Using SectionViewHolder from the other file
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bindData(section)  // Use bindData method from SectionViewHolder

        val adapter = ChecklistAdapter(section.checklist)
        holder.checklistRecyclerView.adapter = adapter

        holder.dropdownBtn.setOnClickListener {
            section.isExpanded = !section.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = sections.size

    fun getPhilippineTime(): Calendar {
        // Set the calendar instance to the Philippine timezone
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
        return calendar
    }

    fun updateSectionBasedOnTime(sections: List<Section>) {
        // Get the current hour of the day in Philippine Time
        val currentHour = getPhilippineTime().get(Calendar.HOUR_OF_DAY)

        // Loop through all sections and set isChecked based on the time
        for (section in sections) {
            when {
                currentHour in 0..5 -> {
                    if (section.label == "After Midnight") section.isExpanded = true
                    else section.isExpanded = false
                }
                currentHour in 6..11 -> {
                    if (section.label == "Morning") section.isExpanded = true
                    else section.isExpanded = false
                }
                currentHour in 12..17 -> {
                    if (section.label == "Afternoon") section.isExpanded = true
                    else section.isExpanded = false
                }
                currentHour in 18..23 -> {
                    if (section.label == "Night") section.isExpanded = true
                    else section.isExpanded = false
                }
            }
        }
    }
}