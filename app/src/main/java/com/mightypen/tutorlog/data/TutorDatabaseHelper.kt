package com.mightypen.tutorlog.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mightypen.tutorlog.model.TutorLog

class TutorDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tutor_log.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_LOGS = "logs"
        const val COLUMN_ID = "id"
        const val COLUMN_STUDENT_NAME = "student_name"
        const val COLUMN_DATE = "date"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_END_TIME = "end_time"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_LOGS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STUDENT_NAME TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_START_TIME TEXT NOT NULL,
                $COLUMN_END_TIME TEXT NOT NULL,
                $COLUMN_NOTES TEXT,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Create index on date and start_time for fast chronological ordering
        db.execSQL("CREATE INDEX idx_logs_date ON $TABLE_LOGS ($COLUMN_DATE DESC, $COLUMN_START_TIME DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database migrations if schema changes in future versions
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOGS")
        onCreate(db)
    }

    fun insertLog(log: TutorLog): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_NAME, log.studentName.trim())
            put(COLUMN_DATE, log.date)
            put(COLUMN_START_TIME, log.startTime)
            put(COLUMN_END_TIME, log.endTime)
            put(COLUMN_NOTES, log.notes.trim())
            put(COLUMN_CREATED_AT, log.createdAt)
        }
        return db.insert(TABLE_LOGS, null, values)
    }

    fun updateLog(log: TutorLog): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_NAME, log.studentName.trim())
            put(COLUMN_DATE, log.date)
            put(COLUMN_START_TIME, log.startTime)
            put(COLUMN_END_TIME, log.endTime)
            put(COLUMN_NOTES, log.notes.trim())
        }
        return db.update(
            TABLE_LOGS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(log.id.toString())
        )
    }

    fun deleteLog(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_LOGS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getAllLogs(): List<TutorLog> {
        val db = readableDatabase
        val list = mutableListOf<TutorLog>()
        val cursor = db.query(
            TABLE_LOGS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE DESC, $COLUMN_START_TIME DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TutorLog(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        studentName = it.getString(it.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                        date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        startTime = it.getString(it.getColumnIndexOrThrow(COLUMN_START_TIME)),
                        endTime = it.getString(it.getColumnIndexOrThrow(COLUMN_END_TIME)),
                        notes = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTES)) ?: "",
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                    )
                )
            }
        }
        return list
    }

    fun getLogsUpToDate(maxDate: String): List<TutorLog> {
        val db = readableDatabase
        val list = mutableListOf<TutorLog>()
        val cursor = db.query(
            TABLE_LOGS,
            null,
            "$COLUMN_DATE <= ?",
            arrayOf(maxDate),
            null,
            null,
            "$COLUMN_DATE DESC, $COLUMN_START_TIME DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TutorLog(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        studentName = it.getString(it.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                        date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        startTime = it.getString(it.getColumnIndexOrThrow(COLUMN_START_TIME)),
                        endTime = it.getString(it.getColumnIndexOrThrow(COLUMN_END_TIME)),
                        notes = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTES)) ?: "",
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                    )
                )
            }
        }
        return list
    }

    fun getDistinctStudentNames(): List<String> {
        val db = readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT DISTINCT $COLUMN_STUDENT_NAME FROM $TABLE_LOGS WHERE $COLUMN_STUDENT_NAME != '' ORDER BY $COLUMN_STUDENT_NAME ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.getString(0))
            }
        }
        return list
    }
}
