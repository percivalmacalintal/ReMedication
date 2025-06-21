package ph.edu.dlsu.ccs.mobicom.remedication

import java.util.*

class Log(date: Date, time: String, name: String, amount: Int, dosage: String, isMissed: Boolean){
    var date = date
        private set
    var time = time
        private set
    var name = name
        private set
    var amount = amount
        private set
    var dosage = dosage
        private set
    private var isMissed = isMissed

    fun getIsMissed(): Boolean{
        return this.isMissed
    }

    fun setIsMissed(isMissed: Boolean){
        this.isMissed = isMissed
    }
}
