package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences._ID))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DATE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_TIME))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_NAME))
            val dosage = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DOSAGE))
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_STATUS))

            val log = Log(
                id,
                date,
                time,
                name,
                dosage,
                LogStatus.fromInt(status)
            )

            logs.add(log)
        }

        cursor.close()

        return logs
    }

    fun getAllLogsSearch(year: String, month: String, day: String, medicine: String): ArrayList<Log> {
        val database: SQLiteDatabase = this.readableDatabase

        val dateArg =  if(month == "%" && day == "%") "$year-%" else "$year-$month-$day"

        android.util.Log.d("LogDbHelper", "dateArg: $dateArg")
        val cursor: Cursor = database.query(
            DbReferences.TABLE_NAME, // Table name
            null, // All columns
            DbReferences.COLUMN_NAME_DATE + " LIKE ? AND " + DbReferences.COLUMN_NAME_NAME + " LIKE ?",
            arrayOf(dateArg, medicine),
            null, // No group by
            null, // No having
            DbReferences.COLUMN_NAME_DATE + " DESC", // Order by date in descending order
            null // No limit
        )

        val logs = ArrayList<Log>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences._ID))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DATE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_TIME))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_NAME))
            val dosage = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DOSAGE))
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_STATUS))

            val log = Log(
                id,
                date,
                time,
                name,
                dosage,
                LogStatus.fromInt(status)
            )

            logs.add(log)
        }

        cursor.close()

        return logs
    }

    @Synchronized
    fun insertLog(log: Log): Long {
        val database = this.writableDatabase

        val values = ContentValues()
        values.put(DbReferences.COLUMN_NAME_DATE, log.getFormattedDate())
        values.put(DbReferences.COLUMN_NAME_TIME, log.time)
        values.put(DbReferences.COLUMN_NAME_NAME, log.name)
        values.put(DbReferences.COLUMN_NAME_DOSAGE, log.dosage)
        values.put(DbReferences.COLUMN_NAME_STATUS, log.status.value)

        val id = database.insert(DbReferences.TABLE_NAME, null, values)


        return id
    }

    @Synchronized
    fun deleteLog(id: Long): Int {
        val database = this.writableDatabase

        val rowsDeleted = database.delete(
            DbReferences.TABLE_NAME,
            "${DbReferences._ID} = ?",
            arrayOf(id.toString())
        )

        database.close()
        return rowsDeleted
    }

    private object DbReferences {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "logs.db"

        const val TABLE_NAME = "logs"
        const val _ID = "id"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_TIME = "time"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_DOSAGE = "dosage"
        const val COLUMN_NAME_STATUS = "status"

        const val CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME_DATE TEXT, " +
                    "$COLUMN_NAME_TIME TEXT, " +
                    "$COLUMN_NAME_NAME TEXT, " +
                    "$COLUMN_NAME_DOSAGE TEXT, " +
                    "$COLUMN_NAME_STATUS INT)"    //Boolean

        const val DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}