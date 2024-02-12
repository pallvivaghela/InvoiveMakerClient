package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.PaymentDTO
import android.widget.EditText
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.View.OnFocusChangeListener
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import java.lang.Exception
import java.util.*

class AddPaymentActivity() : AppCompatActivity(), View.OnClickListener {
    var TAG = PaymentActivity::class.java.name
    private var cancelPayment: TextView? = null
    private var catalogId: Long = 0
    private var deletePayment: TextView? = null
    private var operationType = 0
    private var paidAmount = 0.0
    private var paidAmount1: TextView? = null
    private var paymentDTO: PaymentDTO? = null
    private var paymentId: Long = 0
    private var paymentNotes: EditText? = null
    private var savePayment: TextView? = null
    private var toolbar: Toolbar? = null

    class DatePickerFragment() : DialogFragment(), OnDateSetListener {

        override fun onCreateDialog(bundle: Bundle?): Dialog {
            val instance = Calendar.getInstance()
            return DatePickerDialog((activity)!!, this, instance[1], instance[2], instance[5])
        }

        override fun onDateSet(datePicker: DatePicker, i: Int, i2: Int, i3: Int) {
            val instance = Calendar.getInstance()
            instance[i, i2] = i3
            paymentTimestamp = instance.timeInMillis
            if (paymentDate!!.hasFocus()) {
                paymentDate!!.setText(
                    activity?.let {
                        SettingsDTO.settingsDTO?.let { it1 ->
                            MyConstants.formatDate(
                                it,
                                paymentTimestamp,
                                it1.dateFormat
                            )
                        }
                    }
                )
            }
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_add_payment)
        intentData
        initLayout()
        if (operationType == 1) {
            updateData()
        }
        updateLayout()
    }

    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            paymentId = intent.getLongExtra(MyConstants.PAYMENT_DTO, 0)
            paidAmount = intent.getDoubleExtra(MyConstants.PAID_AMOUNT, 0.0)
            operationType = intent.getIntExtra(MyConstants.OPERATION_TYPE, 0)
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        paymentDTO = PaymentDTO()
        paymentTimestamp = Calendar.getInstance().timeInMillis
        savePayment = findViewById<View>(R.id.save_payment) as TextView
        cancelPayment = findViewById<View>(R.id.cancel_payment) as TextView
        deletePayment = findViewById<View>(R.id.delete_payment) as TextView
        paidAmount1 = findViewById<View>(R.id.paid_amount) as TextView
        paymentDate = findViewById<View>(R.id.payment_date) as EditText
        paymentMethod = findViewById<View>(R.id.payment_method) as EditText
        paymentNotes = findViewById<View>(R.id.payment_notes) as EditText
        paymentDate!!.inputType = 0
        paymentDate!!.setOnClickListener(
            View.OnClickListener
            { showDatePickerDialog() })
        paymentDate!!.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(view: View, z: Boolean) {
                if (z) {
                    showDatePickerDialog()
                }
            }
        }
        cancelPayment!!.setOnClickListener(this)
        savePayment!!.setOnClickListener(this)
        deletePayment!!.setOnClickListener(this)
    }

    fun showDatePickerDialog() {
        DatePickerFragment().show(
            supportFragmentManager, "datePicker"
        )
    }

    private fun updateData() {
        val singlePayment = LoadDatabase.instance?.getSinglePayment(paymentId)
        paymentDTO = singlePayment
        if (singlePayment != null) {
            catalogId = singlePayment.catalogId
        }
        paymentTimestamp = paymentDTO!!.paymentDate!!.toLong()
    }

    @SuppressLint("WrongConstant")
    private fun updateLayout() {
        val i = operationType
        if (i == 0) {
            supportActionBar!!.title = "Add Payment"
            deletePayment!!.visibility = 8
            cancelPayment!!.visibility = 0
        } else if (i == 1) {
            supportActionBar!!.title = "Edit Payment"
            cancelPayment!!.visibility = 8
            deletePayment!!.visibility = 0
        }
        val textView = paidAmount1
        textView!!.text = "" + MyConstants.formatDecimal(java.lang.Double.valueOf(paidAmount))
        paymentDate!!.setText(
            MyConstants.formatDate(
                this,
                Calendar.getInstance().timeInMillis,
                SettingsDTO.settingsDTO!!.dateFormat
            )
        )
        paymentMethod!!.setText("Other")
        val paymentDTO2 = paymentDTO
        if (paymentDTO2 != null && paymentDTO2.id > 0) {
            val textView2 = paidAmount1
            textView2!!.text =
                "" + MyConstants.formatDecimal(java.lang.Double.valueOf(paymentDTO!!.paidAmount))
            paymentDate!!.setText(
                paymentDTO!!.paymentDate?.let {
                    SettingsDTO.settingsDTO?.let { it1 ->
                        MyConstants.formatDate(
                            this,
                            it.toLong(),
                            it1.dateFormat
                        )
                    }
                }
            )
            paymentMethod!!.setText(paymentDTO!!.paymentMethod)
            paymentNotes!!.setText(paymentDTO!!.paymentNotes)
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.cancel_payment) {
            finish()
        } else if (id == R.id.delete_payment) {
            deletePayment()
            finish()
        } else if (id == R.id.save_payment) {
            savePayment()
            finish()
        }
    }

    private fun deletePayment() {
        val paymentDTO2 = paymentDTO
        if (paymentDTO2 != null && paymentDTO2.id > 0) {
            LoadDatabase.instance?.deletePayment(paymentDTO!!)
        }
    }

    private fun savePayment() {
        try {
            paidAmount = java.lang.String.valueOf(paidAmount1!!.text.toString()).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (catalogId == 0L) {
            catalogId = MyConstants.createNewInvoice()
        }
        val paymentDTO2 = PaymentDTO()
        paymentDTO2.paidAmount = paidAmount
        paymentDTO2.catalogId = catalogId
        paymentDTO2.paymentDate = paymentTimestamp.toString()
        paymentDTO2.paymentMethod = paymentMethod!!.text.toString().trim { it <= ' ' }
        paymentDTO2.paymentNotes = paymentNotes!!.text.toString().trim { it <= ' ' }
        val i = operationType
        if (i == 0) {
            LoadDatabase.instance?.addPayment(paymentDTO2)
        } else if (i == 1) {
            paymentDTO2.id = paymentDTO!!.id
            LoadDatabase.instance?.updatePayment(paymentDTO2)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private var paymentDate: EditText? = null
        private var paymentMethod: EditText? = null
        var paymentTimestamp: Long = 0
        fun start(context: Context, j: Long, d: Double, i: Int) {
            val intent = Intent(context, AddPaymentActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.PAID_AMOUNT, d)
            intent.putExtra(MyConstants.OPERATION_TYPE, i)
            context.startActivity(intent)
        }

        @JvmStatic
        fun start(context: Context, j: Long, i: Int) {
            val intent = Intent(context, AddPaymentActivity::class.java)
            intent.putExtra(MyConstants.PAYMENT_DTO, j)
            intent.putExtra(MyConstants.OPERATION_TYPE, i)
            context.startActivity(intent)
        }
    }
}