package ph.edu.dlsu.ccs.mobicom.remedication

class MedicineGenerator {
    companion object{
        fun generateData():ArrayList<Medicine> {
            val tempList = ArrayList<Medicine>()
            tempList.add(Medicine(R.drawable.lisinopril, "Lisinopril", 10, "mg", "Once a day", listOf(0), 1, "January 1, 2025", "December 31, 2026"))
            tempList.add(Medicine(R.drawable.metformin, "Metformin", 500, "mg", "Twice a day", listOf(1,2), 2, "January 2, 2025", "December 30, 2026"))
            tempList.add(Medicine(R.drawable.gabapentin, "Gabapentin", 300, "mg", "Three times a day", listOf(1,2,3), 3, "January 3, 2025", "December 29, 2026"))
            tempList.add(Medicine(R.drawable.maltofer, "Maltofer", 15, "ml", "Once a day", listOf(3), 4, "January 4, 2025", "December 28, 2026"))
            tempList.add(Medicine(R.drawable.lactulose, "Lactulose", 15, "ml", "Twice a day", listOf(0,1), 5, "January 5, 2025", "December 27, 2026"))
            return tempList
        }
    }
}