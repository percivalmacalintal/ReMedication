package ph.edu.dlsu.ccs.mobicom.remedication

class MedicineGenerator {
    companion object{
        fun generateData():ArrayList<Medicine> {
            val tempList = ArrayList<Medicine>()
            tempList.add(Medicine(R.drawable.ic_launcher_background, "1st Medicine", 1, "mg", "Once a day", 1, "January 1, 2025", "December 31, 2026"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "2nd Medicine", 2, "ml", "Twice a day", 2, "January 2, 2025", "December 30, 2026"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "3rd Medicine", 3, "mg", "Three times a day", 3, "January 3, 2025", "December 29, 2026"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "4th Medicine", 4, "ml", "Every other day", 4, "January 4, 2025", "December 28, 2026"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "5th Medicine", 5, "mg", "Once a day", 5, "January 5, 2025", "December 27, 2026"))
            return tempList
        }
    }
}