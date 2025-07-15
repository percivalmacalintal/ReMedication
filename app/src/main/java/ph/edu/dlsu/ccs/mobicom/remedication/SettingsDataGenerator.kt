package ph.edu.dlsu.ccs.mobicom.remedication

class SettingsDataGenerator {
    companion object {
        fun generateData(): ArrayList<Setting> {
            val tempList = ArrayList<Setting>()

            tempList.add(
                Setting(
                    "Early Morning",
                    arrayListOf("12:00 AM", "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM")
                )
            )
            tempList.add(
                Setting(
                    "Morning",
                    arrayListOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM")
                )
            )
            tempList.add(
                Setting(
                    "Afternoon",
                    arrayListOf("12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM")
                )
            )
            tempList.add(
                Setting(
                    "Night",
                    arrayListOf("06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM")
                )
            )
            tempList.add(
                Setting(
                    "Days Left Before Refill",
                    arrayListOf("1 Day", "2 Days", "3 Days", "4 Days", "5 Days", "6 Days", "7 Days")
                )
            )

            return tempList
        }
    }
}