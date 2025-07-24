package ph.edu.dlsu.ccs.mobicom.remedication

class Checklist (id: Long, image: String, medicineName: String, dosage: String, timeOfDay: Int) {
    var id = id
        private set
    var image = image
        private set
    var medicineName = medicineName
        private set
    var dosage = dosage
        private set
    var timeOfDay = timeOfDay
        private set
    var isChecked = false
    var isOverdue = false
}