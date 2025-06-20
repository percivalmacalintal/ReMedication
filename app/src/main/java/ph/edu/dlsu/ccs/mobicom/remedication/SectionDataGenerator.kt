package ph.edu.dlsu.ccs.mobicom.remedication

class SectionDataGenerator {
    companion object{
        fun generateData(): ArrayList<Section> {
            var tempList = ArrayList<Section>()
            tempList.add(Section("After Midnight", ChecklistDataGenerator.generateData()))
            tempList.add(Section("Morning", ChecklistDataGenerator.generateData()))
            tempList.add(Section("Afternoon", ChecklistDataGenerator.generateData()))
            tempList.add(Section("Night", ChecklistDataGenerator.generateData()))
            return tempList
        }
    }
}