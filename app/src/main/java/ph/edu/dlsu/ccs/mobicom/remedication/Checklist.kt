package ph.edu.dlsu.ccs.mobicom.remedication

class Checklist (image: String, medicineName: String, dosage: String) {
    var image = image
        private set
    var medicineName = medicineName
        private set
    var dosage = dosage
        private set
    var isChecked = false
    var isOverdue = false
}