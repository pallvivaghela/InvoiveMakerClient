package com.billcreator.invoice.invoicegenerator.invoicemaker.Database

import android.content.ContentValues
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseManager.Companion.getInstance

class UpdateDatabase private constructor() : DatabaseOpenClose() {
    private var myDb: SQLiteDatabase? = null
    fun updateData() {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", "SYED IKHTIAR AHMED")
        contentValues.put("EMAIL", "IKHTIAR0240@GMAIL.COM")
        contentValues.put("MOBILE", "01716056720")
        contentValues.put("PHONE", "N/A")
        contentValues.put("FAX", "N/A")
        contentValues.put(Contract.Clients.ADDRESS_CONTACT, "N/A")
        contentValues.put("ADDRESS_LINE_1", "N/A")
        contentValues.put("ADDRESS_LINE_2", "N/A")
        contentValues.put("ADDRESS_LINE_3", "N/A")
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_NAME, "N/A")
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_1, "N/A")
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_2, "N/A")
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_3, "N/A")
        try {
            myDb!!.insertOrThrow(Contract.Clients.TABLE_NAME, null, contentValues)
        } catch (e: SQLException) {
            Log.d("Database", e.message!!)
        }
        closeDB()
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseOpenClose
    override fun openDb() {
        if (myDb == null) {
            myDb = getInstance()!!.openDatabase()
        }
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseOpenClose
    override fun closeDB() {
        if (myDb != null) {
            getInstance()!!.closeDatabase()
            myDb = null
        }
    }

    companion object {
        private var mInstance: UpdateDatabase? = null

        @get:Synchronized
        val instance: UpdateDatabase?
            get() {
                var updateDatabase: UpdateDatabase?
                synchronized(UpdateDatabase::class.java) {
                    synchronized(UpdateDatabase::class.java) {
                        if (mInstance == null) {
                            mInstance = UpdateDatabase()
                        }
                        updateDatabase = mInstance
                    }
                    return updateDatabase
                }
            }
    }
}