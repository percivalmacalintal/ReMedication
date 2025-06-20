package ph.edu.dlsu.ccs.mobicom.remedication

class LogsDateGenerator {
    companion object{
        fun generateData(): ArrayList<LogsDate> {
            var tempList = ArrayList<LogsDate>()
            tempList.add(LogsDate("June 20, 2025", LogsGenerator.generateData()))
            tempList.add(LogsDate("June 19, 2025", LogsGenerator.generateData()))
            tempList.add(LogsDate("June 18, 2025", LogsGenerator.generateData()))
            tempList.add(LogsDate("June 17, 2025", LogsGenerator.generateData()))
            return tempList
        }
    }
}