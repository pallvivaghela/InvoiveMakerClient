package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager private constructor(var context: Context) {
    var editor: SharedPreferences.Editor
    val keyPASSCODE = "AppPasscode"
    var pref: SharedPreferences
    var privateMode = 0
    var passcode: Int
        get() = pref.getInt("AppPasscode", -1)
        set(i) {
            editor.putInt("AppPasscode", i)
            editor.commit()
        }

    companion object {
        private const val PREFER_NAME = "InvoiceAndEstimate"
        private var instance: SessionManager? = null
        @JvmStatic
        fun getInstance(context2: Context): SessionManager? {
            if (instance == null) {
                instance = SessionManager(context2)
            }
            return instance
        }
    }

    init {
        val sharedPreferences = context.getSharedPreferences(PREFER_NAME, 0)
        pref = sharedPreferences
        editor = sharedPreferences.edit()
    }
}