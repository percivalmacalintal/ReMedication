package ph.edu.dlsu.ccs.mobicom.remedication

class MedicineGenerator {
    companion object{
        fun generateData():ArrayList<Medicine> {
            val tempList = ArrayList<Medicine>()
            tempList.add(Medicine(android.R.drawable.ic_menu_report_image, "1st Medicine", 1, "mg", "Once a day", 1, "January 1, 2025", "December 31, 2026"))
            tempList.add(Medicine(android.R.drawable.ic_menu_report_image, "2nd Medicine", 2, "ml", "Twice a day", 2, "January 2, 2025", "December 30, 2026"))
            tempList.add(Medicine(android.R.drawable.ic_menu_report_image, "3rd Medicine", 3, "mg", "Three times a day", 3, "January 3, 2025", "December 29, 2026"))
            tempList.add(Medicine(android.R.drawable.ic_menu_report_image, "4th Medicine", 4, "ml", "Once a day", 4, "January 4, 2025", "December 28, 2026"))
            tempList.add(Medicine(android.R.drawable.ic_menu_report_image, "5th Medicine", 5, "mg", "Twice a day", 5, "January 5, 2025", "December 27, 2026"))
            return tempList
        }
    }
}