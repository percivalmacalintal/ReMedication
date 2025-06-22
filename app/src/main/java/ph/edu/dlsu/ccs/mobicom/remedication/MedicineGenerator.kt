package ph.edu.dlsu.ccs.mobicom.remedication

class MedicineGenerator {
    companion object{
        fun generateData():ArrayList<Medicine> {
            val tempList = ArrayList<Medicine>()
            tempList.add(Medicine(R.drawable.medicine, "1st Medicine", 1, "mg", "Once a day", listOf(0), 1, "January 1, 2025", "December 31, 2026"))
            tempList.add(Medicine(R.drawable.medicine, "2nd Medicine", 2, "ml", "Twice a day", listOf(1,2), 2, "January 2, 2025", "December 30, 2026"))
            tempList.add(Medicine(R.drawable.medicine, "3rd Medicine", 3, "mg", "Three times a day", listOf(0,1,2), 3, "January 3, 2025", "December 29, 2026"))
            tempList.add(Medicine(R.drawable.medicine, "4th Medicine", 4, "ml", "Once a day", listOf(2), 4, "January 4, 2025", "December 28, 2026"))
            tempList.add(Medicine(R.drawable.medicine, "5th Medicine", 5, "mg", "Twice a day", listOf(0,1), 5, "January 5, 2025", "December 27, 2026"))
            return tempList
        }
    }
}