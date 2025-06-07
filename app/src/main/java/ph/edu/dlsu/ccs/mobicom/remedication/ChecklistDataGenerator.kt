package ph.edu.dlsu.ccs.mobicom.remedication

class ChecklistDataGenerator {
    companion object{
        fun generateData(): ArrayList<Checklist> {
            var tempList = ArrayList<Checklist>()
            tempList.add(Checklist(R.drawable.medicine, "Guts", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Griffith", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Casca", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Judeau", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Corkus", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Rickert", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Pippin", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Puck", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Isidro", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Farnese", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Serpico", dosage = "12mg"))
            tempList.add(Checklist(R.drawable.medicine, "Schierke", dosage = "12mg"))
            return tempList
        }
    }
}