package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date

class LogDbHelper(context: Context?) : SQLiteOpenHelper(context, DbReferences.DATABASE_NAME, null, DbReferences.DATABASE_VERSION)  {
    // The singleton pattern design
    companion object {
        private var instance: LogDbHelper? = null

        @Synchronized
        fun getInstance(context: Context): LogDbHelper? {
            if (instance == null) {
                instance = LogDbHelper(context.applicationContext)
            }
            return instance
        }
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DbReferences.CREATE_TABLE_STATEMENT)
    }

    // Called when a new version of the DB is present; hence, an "upgrade" to a newer version
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(DbReferences.DROP_TABLE_STATEMENT)
        onCreate(sqLiteDatabase)
    }

    fun getAllLogsDefault(): ArrayList<Log> {
        val database: SQLiteDatabase = this.readableDatabase

        // Query all rows from the logs table
        val cursor: Cursor = database.query(
            DbReferences.TABLE_NAME, // Table name
            null, // All columns
            null, // No where clause
            null, // No where arguments
            null, // No group by
            null, // No having
            DbReferences.COLUMN_NAME_DATE + " DESC", // Order by date in descending order
            null // No limit
        )

        val logs = ArrayList<Log>()

        // Iterate through all the rows in the cursor
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences._ID))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DATE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_TIME))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_NAME))
            val amount = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_AMOUNT))
            val dosage = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DOSAGE))
            val isMissed = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_ISMISSED)) == 1 // Convert 1 to true and 0 to false

            // Create a Log object and add it to the list
            val log = Log(
                id,
                Date(),
                time,
                name,
                amount,
                dosage,
                isMissed
            )

            logs.add(log)
        }

        cursor.close()
        database.close()

        return logs
    }


//    @Synchronized
//    fun insertMedicine(m: Medicine): Long {
//        val database = this.writableDatabase
//
//        // Create a new map of values, where column names are the keys
//        val values = ContentValues()
//        values.put(DbReferences.COLUMN_NAME_IMAGE_ID, m.imageId)
//        values.put(DbReferences.COLUMN_NAME_NAME, m.name)
//        values.put(DbReferences.COLUMN_NAME_DOSAGE, m.dosage)
//        values.put(DbReferences.COLUMN_NAME_UNIT, m.unit)
//        values.put(DbReferences.COLUMN_NAME_FREQUENCY, m.frequency)
//
//        // Convert the List<Int> timeOfDay to a comma-separated string
//        val timeOfDayString = m.timeOfDay.joinToString(",")
//        values.put(DbReferences.COLUMN_NAME_TIME_OF_DAY, timeOfDayString)
//
//        values.put(DbReferences.COLUMN_NAME_REMAINING, m.remaining)
//        values.put(DbReferences.COLUMN_NAME_START, m.getFormattedStartDate())
//        values.put(DbReferences.COLUMN_NAME_END, m.getFormattedEndDate())
//
//        // Insert the new row into the database
//        val id = database.insert(DbReferences.TABLE_NAME, null, values)
//
//        // Close the database
//        database.close()
//
//        return id
//    }
//
//    @Synchronized
//    fun updateMedicine(m: Medicine, id: Long): Int {
//        val database = this.writableDatabase
//
//        // Create a new ContentValues object to hold the updated values
//        val values = ContentValues()
//        values.put(DbReferences.COLUMN_NAME_IMAGE_ID, m.imageId)
//        values.put(DbReferences.COLUMN_NAME_NAME, m.name)
//        values.put(DbReferences.COLUMN_NAME_DOSAGE, m.dosage)
//        values.put(DbReferences.COLUMN_NAME_UNIT, m.unit)
//        values.put(DbReferences.COLUMN_NAME_FREQUENCY, m.frequency)
//
//        // Convert timeOfDay list to a comma-separated string
//        val timeOfDayString = m.timeOfDay.joinToString(",")
//        values.put(DbReferences.COLUMN_NAME_TIME_OF_DAY, timeOfDayString)
//
//        values.put(DbReferences.COLUMN_NAME_REMAINING, m.remaining)
//        values.put(DbReferences.COLUMN_NAME_START, m.getFormattedStartDate())
//        values.put(DbReferences.COLUMN_NAME_END, m.getFormattedEndDate())
//
//        // Perform the update operation
//        val rowsAffected = database.update(
//            DbReferences.TABLE_NAME,
//            values,
//            "${DbReferences._ID} = ?", // Update the row with the specified ID
//            arrayOf(id.toString())
//        )
//
//        // Close the database connection
//        database.close()
//
//        return rowsAffected // Return the number of rows affected by the update
//    }
//
//    @Synchronized
//    fun deleteMedicine(id: Long): Int {
//        val database = this.writableDatabase
//
//        val rowsDeleted = database.delete(
//            DbReferences.TABLE_NAME,
//            "${DbReferences._ID} = ?",
//            arrayOf(id.toString())
//        )
//
//        database.close()
//        return rowsDeleted
//    }

    private object DbReferences {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "my_database.db"

        const val TABLE_NAME = "logs"
        const val _ID = "id"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_TIME = "time"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_AMOUNT = "amount"
        const val COLUMN_NAME_DOSAGE = "dosage"
        const val COLUMN_NAME_ISMISSED = "isMissed"

        const val CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME_DATE TEXT, " +
                    "$COLUMN_NAME_TIME TEXT, " +
                    "$COLUMN_NAME_NAME TEXT, " +
                    "$COLUMN_NAME_AMOUNT INT, " +
                    "$COLUMN_NAME_DOSAGE TEXT, " +
                    "$COLUMN_NAME_ISMISSED INT)"    //Boolean

        const val DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

}