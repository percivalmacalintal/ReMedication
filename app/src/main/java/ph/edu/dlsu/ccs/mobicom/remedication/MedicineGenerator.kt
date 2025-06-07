package ph.edu.dlsu.ccs.mobicom.remedication

class MedicineGenerator {
    companion object{
        fun generateData():ArrayList<Medicine> {
            var tempList = ArrayList<Medicine>()
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            tempList.add(Medicine(R.drawable.ic_launcher_background, "MED NAME", "NUM REMAINS"))
            return tempList
        }
    }
}