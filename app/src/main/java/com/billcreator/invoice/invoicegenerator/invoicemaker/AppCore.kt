package com.billcreator.invoice.invoicegenerator.invoicemaker

import android.app.Application
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseHelper
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseManager.Companion.initializeInstance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO.Companion.settingsDTO

class AppCore : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        init()
    }

    fun init() {
        initializeInstance(DatabaseHelper(applicationContext))
        settingsDTO = LoadDatabase.instance!!.settings
        LoadDatabase.instance!!.viewData()
    }

    companion object {
        var mInstance: AppCore? = null

        @get:Synchronized
        val instance: AppCore?
            get() {
                var appCore: AppCore?
                synchronized(AppCore::class.java) {
                    synchronized(AppCore::class.java) { appCore = mInstance }
                    return appCore
                }
            }
    }
}