package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.SessionManager.Companion.getInstance

class LockScreen : Activity(), View.OnClickListener, View.OnKeyListener {
    private var cancelBtn: Button? = null
    private var focusKeyboard: EditText? = null
    private var forgetPassword: Button? = null
    private var mPIN = ""
    private var pass = 0
    private var saveBtn: Button? = null
    private var type = 0
    private var watcher: MyTextWtcher? = null

    inner class MyTextWtcher(private val et: EditText?) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {}
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        @SuppressLint("WrongConstant")
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            if (R.id.EditText05 == et!!.id) {
                if (mPIN.length == 4) {
                    mPIN = ""
                    mPINdisplay()
                } else {
                    val lockScreen = this@LockScreen
                    lockScreen.mPIN = mPIN + focusKeyboard!!.text.toString()
                    Log.e("44444", "onTextChanged: " + lockScreen.mPIN)
                    mPINdisplay()
                }
                if (type == 1) {
                    if (mPIN.length == 4) {
                        saveBtn!!.visibility = 0
                    } else {
                        saveBtn!!.visibility = 4
                    }
                }
                if (type == 2 && mPIN.length == 4 && pass >= 0 && mPIN.toInt() == pass) {
                    finish()
                }
                focusKeyboard!!.removeTextChangedListener(watcher)
                focusKeyboard!!.setText("")
                focusKeyboard!!.addTextChangedListener(watcher)
            }
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.lock_screen)
        init()
    }

    @SuppressLint("WrongConstant")
    private fun init() {
        cancelBtn = findViewById<View>(R.id.Button01) as Button
        saveBtn = findViewById<View>(R.id.Button02) as Button
        focusKeyboard = findViewById<View>(R.id.EditText05) as EditText
        val button = findViewById<View>(R.id.Button03) as Button
        forgetPassword = button
        button.visibility = 4
        saveBtn!!.visibility = 4
        val intExtra = intent.getIntExtra("ScreenType", 0)
        type = intExtra
        if (intExtra == 2) {
            cancelBtn!!.visibility = 4
            pass = intent.getIntExtra("AppPasscode", -1)
        }
        val myTextWtcher = MyTextWtcher(focusKeyboard)
        watcher = myTextWtcher
        focusKeyboard!!.addTextChangedListener(myTextWtcher)
        focusKeyboard!!.setOnKeyListener(this)
        cancelBtn!!.setOnClickListener(this)
        saveBtn!!.setOnClickListener(this)
    }

    fun mPINdisplay() {
        val editText = findViewById<View>(R.id.EditText01) as EditText
        val editText2 = findViewById<View>(R.id.EditText02) as EditText
        val editText3 = findViewById<View>(R.id.EditText03) as EditText
        val editText4 = findViewById<View>(R.id.EditText04) as EditText
        if (mPIN.length > 0) {
            editText.setText("*")
        }
        if (mPIN.length > 1) {
            editText2.setText("*")
        }
        if (mPIN.length > 2) {
            editText3.setText("*")
        }
        if (mPIN.length > 3) {
            editText4.setText("*")
        }
        if (mPIN.length < 4) {
            editText4.setText("")
        }
        if (mPIN.length < 3) {
            editText3.setText("")
        }
        if (mPIN.length < 2) {
            editText2.setText("")
        }
        if (mPIN.length < 1) {
            editText.setText("")
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.Button01 -> {
                finish()
                return
            }
            R.id.Button02 -> {
                getInstance(
                    applicationContext
                )!!.passcode = mPIN.toInt()
                setResult(-1)
                instance!!.notifyListeners(null, 666)
                finish()
                return
            }
            else -> return
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    @SuppressLint("WrongConstant")
    override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
        var currentFocus: View? = null
        if (keyEvent.action != 0) {
            return true
        }
        if (i == 67) {
            if (mPIN.length >= 1 && mPIN.length <= 4) {
                val str = mPIN
                mPIN = str.substring(0, str.length - 1)
                mPINdisplay()
                if (type == 1) {
                    if (mPIN.length == 4) {
                        saveBtn!!.visibility = 0
                    } else {
                        saveBtn!!.visibility = 4
                    }
                }
                if (type == 2 && mPIN.length == 4 && pass >= 0 && mPIN.toInt() == pass) {
                    finish()
                }
            }
        } else if (i == 66 && getCurrentFocus().also { currentFocus = it!! } != null) {
            Toast.makeText(applicationContext, "Wrong Pass code", 0).show()
            (getSystemService("input_method") as InputMethodManager).hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                0
            )
        }
        return false
    }
}