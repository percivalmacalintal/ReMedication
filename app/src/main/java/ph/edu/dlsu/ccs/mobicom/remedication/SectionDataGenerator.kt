package ph.edu.dlsu.ccs.mobicom.remedication

class SectionDataGenerator {
    companion object {
        fun generateData(): ArrayList<Section> {
            val sections = ArrayList<Section>()
            val medicineList = MedicineGenerator.generateData()

            val sectionTitles = listOf("After Midnight", "Morning", "Afternoon", "Night")

            for (i in 0..3) {
                val filteredMedicines = medicineList.filter { it.timeOfDay.contains(i) }

                if (filteredMedicines.isNotEmpty()) {
                    val checklistItems = filteredMedicines.map {
                        Checklist(it.imageId, it.name, "${it.dosage} ${it.unit}")
                    }
                    sections.add(Section(sectionTitles[i], ArrayList(checklistItems)))
                }
            }

            return sections
        }
    }
}