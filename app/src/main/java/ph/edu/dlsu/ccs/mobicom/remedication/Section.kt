package ph.edu.dlsu.ccs.mobicom.remedication

class Section (label: String, checklist: ArrayList<Checklist>) {
    var label = label
        private set
    var checklist = checklist
        private set
    var isExpanded = true
}