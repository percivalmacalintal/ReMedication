package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SectionDataGenerator {
    companion object {
        private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

        // Method to generate data asynchronously
        fun generateData(context: Context, callback: (ArrayList<Section>) -> Unit) {
            executorService.execute {
                // Perform the database operation in the background
                val sections = ArrayList<Section>()
                val myDbHelper = MedicineDbHelper.getInstance(context)
                val medicineList = myDbHelper?.getAllMedicinesDefault() ?: emptyList()

                val sectionTitles = listOf("Early Morning", "Morning", "Afternoon", "Night")

                for (i in 0..3) {
                    val filteredMedicines = medicineList.filter { it.timeOfDay.contains(i) }

                    if (filteredMedicines.isNotEmpty()) {
                        val checklistItems = filteredMedicines.map {
                            Checklist(it.imageId, it.name, "${it.dosage} ${it.unit}")
                        }
                        sections.add(Section(sectionTitles[i], ArrayList(checklistItems)))
                    }
                }

                // Use Handler to post the result back to the UI thread
                Handler(Looper.getMainLooper()).post {
                    callback(sections)
                }
            }
        }
    }
}