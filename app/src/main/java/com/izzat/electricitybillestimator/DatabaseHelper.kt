package com.izzat.electricitybillestimator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ElectricityBills.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "bills"
        const val COL_ID = "id"
        const val COL_MONTH = "month"
        const val COL_UNITS = "units"
        const val COL_REBATE = "rebate"
        const val COL_TOTAL = "total"
        const val COL_FINAL = "final"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MONTH TEXT,
                $COL_UNITS INTEGER,
                $COL_REBATE REAL,
                $COL_TOTAL REAL,
                $COL_FINAL REAL
            )
        """.trimIndent()
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertBill(month: String, units: Int, rebate: Double, total: Double, final: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_MONTH, month)
            put(COL_UNITS, units)
            put(COL_REBATE, rebate)
            put(COL_TOTAL, total)
            put(COL_FINAL, final)
        }
        val result = db.insert(TABLE_NAME, null, values)
        return result != -1L
    }

    fun getAllBills(): List<Map<String, String>> {
        val list = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val item = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)).toString(),
                    "month" to cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH)),
                    "final" to "Final: RM %.2f".format(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL))
                    )
                )
                list.add(item)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    fun getBillById(id: Int): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?", arrayOf(id.toString()))
        var bill: Map<String, String>? = null

        if (cursor.moveToFirst()) {
            bill = mapOf(
                "month" to cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH)),
                "units" to cursor.getInt(cursor.getColumnIndexOrThrow(COL_UNITS)).toString(),
                "rebate" to "%.2f%%".format(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_REBATE))),
                "total" to "RM %.2f".format(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL))),
                "final" to "RM %.2f".format(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL)))
            )
        }

        cursor.close()
        return bill
    }
}
