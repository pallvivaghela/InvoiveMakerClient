package com.billcreator.invoice.invoicegenerator.invoicemaker.Database

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null as CursorFactory?, 1) {
    override fun onUpgrade(sQLiteDatabase: SQLiteDatabase, i: Int, i2: Int) {}
    override fun onCreate(sQLiteDatabase: SQLiteDatabase) {

        sQLiteDatabase.execSQL(
            "CREATE TABLE BUSINESS_INFORMATION (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "REGISTRATION_NUMBER TEXT," +
                    "VAT TEXT," +
                    "ADDRESS_LINE_1 TEXT," +
                    "ADDRESS_LINE_2 TEXT," +
                    "ADDRESS_LINE_3 TEXT," +
                    "PHONE TEXT," +
                    "MOBILE TEXT," +
                    "FAX TEXT," +
                    "EMAIL TEXT," +
                    "WEBSITE TEXT," +
                    "LOGO_URL TEXT," +
                    "PAYPAL_ADDRESS TEXT," +
                    "CHEQUES_INFORMATION TEXT," +
                    "BANK_INFORMATION TEXT," +
                    "OTHER_PAYMENT_INFORMATION TEXT," +
                    "TAXES INTEGER,FULLY_PAID INTEGER," +
                    "INVOICE_NOTES TEXT," +
                    "ESTIMATE_NOTES TEXT,QTY_RATE INTEGER ) ")

        sQLiteDatabase.execSQL(
            "CREATE TABLE CLIENTS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "EMAIL TEXT," +
                    "MOBILE TEXT," +
                    "PHONE TEXT," +
                    "FAX TEXT," +
                    "ADDRESS_CONTACT TEXT," +
                    "ADDRESS_LINE_1 TEXT," +
                    "ADDRESS_LINE_2 TEXT," +
                    "ADDRESS_LINE_3 TEXT," +
                    "SHIPPING_ADDRESS_NAME TEXT," +
                    "SHIPPING_ADDRESS_LINE_1 TEXT," +
                    "SHIPPING_ADDRESS_LINE_2 TEXT," +
                    "SHIPPING_ADDRESS_LINE_3 TEXT ) ")

        sQLiteDatabase.execSQL(
            "CREATE TABLE CATALOG (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "CLIENTS_ID INTEGER DEFAULT 0," +
                    "TYPE INTEGER,DISCOUNT_TYPE INTEGER," +
                    "TAX_TYPE INTEGER,TAX_LABEL TEXT," +
                    "TAX_RATE REAL,NAME TEXT," +
                    "CREATED_AT TEXT," +
                    "TERMS INTEGER," +
                    "DUE_DATE TEXT," +
                    "PO_NUMBER TEXT," +
                    "DISCOUNT REAL," +
                    "PAID REAL," +
                    "TOTAL_AMOUNT REAL," +
                    "PAID_STATUS INTEGER," +
                    "ESTIMATE_STATUS INTEGER," +
                    "SIGNED_DATE TEXT," +
                    "SIGNED_URL TEXT," +
                    "NOTES TEXT ) ")

        sQLiteDatabase.execSQL("CREATE TABLE CATALOG_IMAGES (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CATALOG_ID INTEGER," +
                "IMAGE_URL TEXT," +
                "DESCRIPTION TEXT," +
                "ADDITIONAL_DETAILS TEXT," +
                " FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ")

        sQLiteDatabase.execSQL("CREATE TABLE ITEMS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT," +
                "DESCRIPTION TEXT," +
                "UNIT_COST REAL,TAXABLE INTEGER )")

        sQLiteDatabase.execSQL("CREATE TABLE ITEMS_ASSOCIATED (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ITEM_NAME TEXT," +
                "DESCRIPTION TEXT," +
                "UNIT_COST REAL," +
                "TAXABLE INTEGER," +
                "CATALOG_ID INTEGER," +
                "QTY REAL DEFAULT 1," +
                "TAX_RATE REAL," +
                "DISCOUNT REAL, " +
                "FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ")

        sQLiteDatabase.execSQL("CREATE TABLE SHIPPING (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CATALOG_ID INTEGER," +
                "AMOUNT REAL," +
                "SHIPPING_DATE TEXT," +
                "SHIP_VIA TEXT," +
                "TRACKING TEXT," +
                "FOB TEXT," +
                " FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ")

        sQLiteDatabase.execSQL("CREATE TABLE PAYMENTS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CATALOG_ID INTEGER," +
                "AMOUNT TEXT," +
                "DATE TEXT," +
                "METHOD TEXT," +
                "NOTES TEXT, " +
                "FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ")

        sQLiteDatabase.execSQL("CREATE TABLE SETTINGS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CURRENCY INTEGER DEFAULT 102 ," +
                "DATE_FORMAT INTEGER DEFAULT 0  ) ")

        populateSettingsTable(sQLiteDatabase)
    }

    fun populateSettingsTable(sQLiteDatabase: SQLiteDatabase) {
        val contentValues = ContentValues()
        contentValues.put(Contract.Settings.CURRENCY, 102)
        contentValues.put(Contract.Settings.DATE_FORMAT, 0)
        try {
            sQLiteDatabase.insertOrThrow(Contract.Settings.TABLE_NAME, null, contentValues)
        } catch (unused: SQLException) {
        }
    }

    @Throws(IOException::class)
    fun copyDbOperation(str: String?, str2: String?): Boolean {
        close()
        val file = File(str)
        val file2 = File(str2)
        if (!file.exists()) {
            return false
        }
        copyFile(FileInputStream(file), FileOutputStream(file2))
        writableDatabase.close()
        return true
    }

    override fun onConfigure(sQLiteDatabase: SQLiteDatabase) {
        sQLiteDatabase.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        const val DATABASE_NAME = "invoiceAndEstimate.exdb"
        const val DATABASE_VERSION = 1

        @Throws(IOException::class)
        fun copyFile(fileInputStream: FileInputStream, fileOutputStream: FileOutputStream) {
            var th: Throwable?
            var fileChannel: FileChannel?
            var fileChannel2: FileChannel? = null
            try {
                val channel = fileInputStream.channel
                try {
                    fileChannel2 = fileOutputStream.channel
                    fileChannel2.transferFrom(channel, 0, channel!!.size())
                    if (channel != null) {
                        channel.close()
                    }
                    fileChannel2?.close()
                } catch (th2: Throwable) {
                    th = th2
                    fileChannel2 = channel
                    fileChannel = fileChannel2
                    if (fileChannel2 != null) {
                    }
                    if (fileChannel != null) {
                    }
                    throw th
                }
            } catch (th3: Throwable) {
                th = th3
                fileChannel = null
                fileChannel2?.close()
                if (fileChannel != null) {
                    fileChannel.close()
                }
                try {
                    throw th
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}