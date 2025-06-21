package ph.edu.dlsu.ccs.mobicom.remedication

class SettingsDataGenerator {
    companion object {
        fun generateData(): ArrayList<Settings> {
            val tempList = ArrayList<Settings>()

            tempList.add(
                Settings(
                    "After Midnight",
                    arrayListOf("12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM")
                )
            )
            tempList.add(
                Settings(
                    "Morning",
                    arrayListOf("6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM")
                )
            )
            tempList.add(
                Settings(
                    "Afternoon",
                    arrayListOf("12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM")
                )
            )
            tempList.add(
                Settings(
                    "Night",
                    arrayListOf("6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM")
                )
            )
            tempList.add(
                Settings(
                    "Days Left Before Refill",
                    arrayListOf("1 Day", "2 Days", "3 Days", "4 Days", "5 Days", "6 Days", "7 Days")
                )
            )

            return tempList
        }
    }
}