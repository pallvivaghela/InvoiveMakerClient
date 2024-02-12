package com.billcreator.invoice.invoicegenerator.invoicemaker.Database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager {
    private var mDatabase: SQLiteDatabase? = null
    private var mOpenCounter = 0
    @Synchronized
    fun openDatabase(): SQLiteDatabase? {
        val i = mOpenCounter + 1
        mOpenCounter = i
        if (i == 1) {
            mDatabase = mDatabaseHelper!!.writableDatabase
        }
        return mDatabase
    }

    @Synchronized
    fun closeDatabase() {
        val i = mOpenCounter - 1
        mOpenCounter = i
        if (i == 0) {
            mDatabase!!.close()
        }
    }

    companion object {
        private var instance: DatabaseManager? = null
        private var mDatabaseHelper: SQLiteOpenHelper? = null
        @JvmStatic
        @Synchronized
        fun initializeInstance(sQLiteOpenHelper: SQLiteOpenHelper?) {
            synchronized(DatabaseManager::class.java) {
                synchronized(DatabaseManager::class.java) {
                    if (instance == null) {
                        instance = DatabaseManager()
                        mDatabaseHelper = sQLiteOpenHelper
                    }
                }
            }
        }

        @JvmStatic
        @Synchronized
        fun getInstance(): DatabaseManager? {
            var databaseManager: DatabaseManager?
            synchronized(DatabaseManager::class.java) {
                synchronized(DatabaseManager::class.java) {
                    databaseManager = instance
                    checkNotNull(databaseManager) { "DatabaseManager" + " is not initialized, call initializeInstance(..) method first." }
                }
                return databaseManager
            }
        }
    }
}