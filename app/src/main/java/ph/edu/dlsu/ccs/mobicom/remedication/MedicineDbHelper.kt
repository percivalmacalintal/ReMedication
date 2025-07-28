package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MedicineDbHelper(context: Context?) : SQLiteOpenHelper(context, DbReferences.DATABASE_NAME, null, DbReferences.DATABASE_VERSION) {
    companion object {
        private var instance: MedicineDbHelper? = null

        @Synchronized
        fun getInstance(context: Context): MedicineDbHelper? {
            if (instance == null) {
                instance = MedicineDbHelper(context.applicationContext)
            }
            return instance
        }
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DbReferences.CREATE_TABLE_STATEMENT)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(DbReferences.DROP_TABLE_STATEMENT)
        onCreate(sqLiteDatabase)
    }

    fun getAllMedicinesDefault(): ArrayList<Medicine> {
        val database: SQLiteDatabase = this.readableDatabase

        val cursor: Cursor = database.query(
            DbReferences.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            DbReferences._ID + " ASC", // Order by ID
            null
        )

        val medicines = ArrayList<Medicine>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences._ID))
            val imageId = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_IMAGE_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_NAME))
            val dosage = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_DOSAGE))
            val unit = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_UNIT))
            val frequency = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_FREQUENCY))
            val timeOfDayStr = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_TIME_OF_DAY))
            val remaining = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_REMAINING))
            val start = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_START))
            val end = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.COLUMN_NAME_END))

            val timeOfDay: List<Int> = timeOfDayStr // Convert comma-separated timeOfDay string to List<Int>
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }

            val medicine = Medicine(
                id,
                imageId,
                name,
                dosage,
                unit,
                frequency,
                timeOfDay,
                remaining,
                start,
                end
            )

            medicines.add(medicine)
        }

        cursor.close()
        database.close()

        return medicines
    }

    @Synchronized
    fun insertMedicine(m: Medicine): Long {
        val database = this.writableDatabase

        val values = ContentValues()
        values.put(DbReferences.COLUMN_NAME_IMAGE_ID, m.imageId)
        values.put(DbReferences.COLUMN_NAME_NAME, m.name)
        values.put(DbReferences.COLUMN_NAME_DOSAGE, m.dosage)
        values.put(DbReferences.COLUMN_NAME_UNIT, m.unit)
        values.put(DbReferences.COLUMN_NAME_FREQUENCY, m.frequency)

        val timeOfDayString = m.timeOfDay.joinToString(",")
        values.put(DbReferences.COLUMN_NAME_TIME_OF_DAY, timeOfDayString)

        values.put(DbReferences.COLUMN_NAME_REMAINING, m.remaining)
        values.put(DbReferences.COLUMN_NAME_START, m.getFormattedStartDate())
        values.put(DbReferences.COLUMN_NAME_END, m.getFormattedEndDate())

        val id = database.insert(DbReferences.TABLE_NAME, null, values)

        database.close()

        return id
    }

    @Synchronized
    fun updateMedicine(m: Medicine, id: Long): Int {
        val database = this.writableDatabase

        val values = ContentValues()
        values.put(DbReferences.COLUMN_NAME_IMAGE_ID, m.imageId)
        values.put(DbReferences.COLUMN_NAME_NAME, m.name)
        values.put(DbReferences.COLUMN_NAME_DOSAGE, m.dosage)
        values.put(DbReferences.COLUMN_NAME_UNIT, m.unit)
        values.put(DbReferences.COLUMN_NAME_FREQUENCY, m.frequency)

        val timeOfDayString = m.timeOfDay.joinToString(",")
        values.put(DbReferences.COLUMN_NAME_TIME_OF_DAY, timeOfDayString)

        values.put(DbReferences.COLUMN_NAME_REMAINING, m.remaining)
        values.put(DbReferences.COLUMN_NAME_START, m.getFormattedStartDate())
        values.put(DbReferences.COLUMN_NAME_END, m.getFormattedEndDate())

        val rowsAffected = database.update(
            DbReferences.TABLE_NAME,
            values,
            "${DbReferences._ID} = ?", // Update the row with the specified ID
            arrayOf(id.toString())
        )

        database.close()

        return rowsAffected
    }

    @Synchronized
    fun deleteMedicine(id: Long): Int {
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
        const val DATABASE_NAME = "medicines.db"

        const val TABLE_NAME = "medicines"
        const val _ID = "id"
        const val COLUMN_NAME_IMAGE_ID = "image_id"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_DOSAGE = "dosage"
        const val COLUMN_NAME_UNIT = "unit"
        const val COLUMN_NAME_FREQUENCY = "frequency"
        const val COLUMN_NAME_TIME_OF_DAY = "time_of_day" // Stored as comma-separated values
        const val COLUMN_NAME_REMAINING = "remaining"
        const val COLUMN_NAME_START = "start"
        const val COLUMN_NAME_END = "end"

        const val CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME_IMAGE_ID TEXT, " +
                    "$COLUMN_NAME_NAME TEXT, " +
                    "$COLUMN_NAME_DOSAGE INTEGER, " +
                    "$COLUMN_NAME_UNIT TEXT, " +
                    "$COLUMN_NAME_FREQUENCY TEXT, " +
                    "$COLUMN_NAME_TIME_OF_DAY TEXT, " + // Store as comma-separated string
                    "$COLUMN_NAME_REMAINING INTEGER, " +
                    "$COLUMN_NAME_START TEXT, " +
                    "$COLUMN_NAME_END TEXT)"

        const val DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS $TABLE_NAME"
    }


}