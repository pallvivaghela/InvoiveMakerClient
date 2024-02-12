package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.CatalogDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import java.util.*

class InvoiceInfoActivity() : AppCompatActivity(), View.OnClickListener {
    private var catalogDTO: CatalogDTO? = null
    private var invoiceName: EditText? = null
    private var poNumber: EditText? = null
    var saveBtn: Button? = null
    private var settingsDTO: SettingsDTO? = null
    var termsArray = arrayOf(
        "None",
        "Due on receipt",
        "Next day",
        "2 Days",
        "3 Days",
        "4 Days",
        "5 Days",
        "6 Days",
        "7 Days",
        "10 Days",
        "14 Days",
        "30 Days",
        "45 Days",
        "60 Days",
        "90 Days",
        "180 Days",
        "365 Days",
        "KeyAttributes"
    )
    private var toolbar: Toolbar? = null
    override fun onClick(view: View) {}
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_invoice_info)
        intentData
        initLayout()
        saveBtn!!.setOnClickListener(
            View.OnClickListener
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass1 */
            {
                updateOnBackPressed()
                onBackPressed()
            })
        invoiceName!!.addTextChangedListener(object : TextWatcher {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass2 */
            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                updateOnBackPressed()
            }
        })
    }

    private val intentData: Unit
        private get() {
            catalogDTO = intent.getSerializableExtra(MyConstants.CATALOG_DTO) as CatalogDTO?
            settingsDTO = SettingsDTO.settingsDTO
        }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.title = "Invoice Info"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        invoiceName = findViewById<View>(R.id.invoice_name) as EditText
        saveBtn = findViewById<View>(R.id.save_btn) as Button
        poNumber = findViewById<View>(R.id.po_number) as EditText
        val editText = findViewById<View>(R.id.creation_date) as EditText
        creationDate = editText
        editText.inputType = 0
        creationDate!!.setOnClickListener(object : View.OnClickListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass3 */
            override fun onClick(view: View) {
                showDatePickerDialog()
            }
        })
        creationDate!!.onFocusChangeListener = object : OnFocusChangeListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass4 */
            override fun onFocusChange(view: View, z: Boolean) {
                if (z) {
                    showDatePickerDialog()
                }
            }
        }
        val editText2 = findViewById<View>(R.id.due_date) as EditText
        dueDate = editText2
        editText2.inputType = 0
        dueDate!!.setOnClickListener(object : View.OnClickListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass5 */
            override fun onClick(view: View) {
                showDatePickerDialog()
            }
        })
        dueDate!!.onFocusChangeListener = object : OnFocusChangeListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass6 */
            override fun onFocusChange(view: View, z: Boolean) {
                if (z) {
                    showDatePickerDialog()
                }
            }
        }
        val editText3 = findViewById<View>(R.id.terms) as EditText
        terms = editText3
        editText3.inputType = 0
        terms!!.setOnClickListener(object : View.OnClickListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass7 */
            override fun onClick(view: View) {
                showTermsDialog()
            }
        })
        terms!!.onFocusChangeListener = object : OnFocusChangeListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass8 */
            override fun onFocusChange(view: View, z: Boolean) {
                if (z) {
                    showTermsDialog()
                }
            }
        }
        if (MyConstants.CATALOG_TYPE == 1) {
            supportActionBar!!.title = "Estimate Info"
            terms!!.visibility = 8
            poNumber!!.visibility = 8
            dueDate!!.visibility = 8
        }
        if (catalogDTO!!.id > 0) {
            invoiceName!!.setText(catalogDTO!!.catalogName)
            val parseLong = catalogDTO!!.creationDate!!.toLong()
            creationDateTimestamp = parseLong
            creationDate!!.setText(
                MyConstants.formatDate(
                    this,
                    parseLong,
                    settingsDTO!!.dateFormat
                )
            )
            val terms2 = catalogDTO!!.terms
            position = terms2
            if (terms2 == 0 || terms2 == 1) {
                dueDate!!.visibility = 8
            } else {
                val parseLong2 = catalogDTO!!.dueDate!!.toLong()
                dueDateTimestamp = parseLong2
                dueDate!!.setText(
                    MyConstants.formatDate(
                        this,
                        parseLong2,
                        settingsDTO!!.dateFormat
                    )
                )
            }
            dateFormat = settingsDTO!!.dateFormat
            terms!!.setText(termsArray[position])
            poNumber!!.setText(catalogDTO!!.poNumber)
            return
        }
        addInvoiceInfo()
    }

    private fun addInvoiceInfo() {
        invoiceName!!.setText(MyConstants.invoiceName)
        val timeInMillis = Calendar.getInstance().timeInMillis
        creationDateTimestamp = timeInMillis
        creationDate!!.setText(MyConstants.formatDate(this, timeInMillis, settingsDTO!!.dateFormat))
        terms!!.setText(resources.getString(R.string.due_on_receipt))
        dueDate!!.setText("")
        poNumber!!.setText("")
        position = 1
    }

    fun showTermsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setSingleChoiceItems(
            termsArray,
            position,
            object : DialogInterface.OnClickListener {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.AnonymousClass9 */
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    position = i
                    val unused = position
                    terms!!.setText(termsArray[position])
                    updateDueDate()
                    dialogInterface.dismiss()
                }
            })
        builder.show()
    }

    private fun updateDueDate() {
        val i = position
        if (i == 0 || i == 1) {
            Log.e("444444", "updateDueDate: 1")
            dueDate!!.visibility = 8
            dueDate!!.setText("")
            return
        }
        dueDate!!.visibility = 0
        val i2 = position
        if (i2 == 17) {
            dueDateTimestamp = creationDateTimestamp
            dueDate!!.setText(creationDate!!.text.toString())
            return
        }
        try {
            calendar.timeInMillis =
                creationDateTimestamp + (((termsArrayNumeric.get(i2) * 24 * 60 * 60).toLong()) * 1000)
            dueDateTimestamp = calendar.timeInMillis
            val formatDate =
                MyConstants.formatDate(applicationContext, dueDateTimestamp, dateFormat)
            dueText = formatDate
            dueDate!!.setText(formatDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showDatePickerDialog() {
        DatePickerFragment().show(
            supportFragmentManager, "datePicker"
        )
    }

    private fun updateOnBackPressed() {
        val obj = invoiceName!!.text.toString()
        invoiceName!!.requestFocus()
        if (obj.trim { it <= ' ' }.equals("", ignoreCase = true)) {
            invoiceName!!.error = "Cannot be empty"
        } else if (Character.isDigit(obj.trim { it <= ' ' }[obj.length - 1])) {
            catalogDTO!!.catalogName = invoiceName!!.text.toString().trim { it <= ' ' }
            catalogDTO!!.creationDate = creationDateTimestamp.toString()
            catalogDTO!!.terms = position
            val j = dueDateTimestamp
            if (j != 0L) {
                catalogDTO!!.dueDate = j.toString()
            } else {
                catalogDTO!!.dueDate = ""
            }
            catalogDTO!!.poNumber = poNumber!!.text.toString().trim { it <= ' ' }
            if (catalogDTO!!.id == 0L) {
                catalogDTO!!.paidStatus = 1
                catalogDTO!!.discountType = 0
                catalogDTO!!.taxType = 3
                LoadDatabase.instance!!.saveInvoice(catalogDTO!!)
                return
            }
            LoadDatabase.instance!!.updateInvoice(catalogDTO!!)
        } else {
            invoiceName!!.error = "Invoice should be letters followed by digits"
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class DatePickerFragment() : DialogFragment(), OnDateSetListener {

        override fun onCreateDialog(bundle: Bundle?): Dialog {
            val instance = Calendar.getInstance()
            if (creationDate!!.hasFocus()) {
                instance.timeInMillis = creationDateTimestamp
            } else if (dueDate!!.hasFocus()) {
                instance.timeInMillis = dueDateTimestamp
            }
            return DatePickerDialog((activity)!!, this, instance[1], instance[2], instance[5])
        }

        override fun onDateSet(datePicker: DatePicker, i: Int, i2: Int, i3: Int) {
            val instance = Calendar.getInstance()
            instance[i, i2] = i3
            val formatDate =
                context?.let { MyConstants.formatDate(it, instance.timeInMillis, dateFormat) }
            if (creationDate!!.hasFocus()) {
                creationDate!!.setText(formatDate)
                creationDateTimestamp = instance.timeInMillis
                val unused = creationDateTimestamp
                if (position != 17) {
                    try {
                        calendar.timeInMillis = creationDateTimestamp + (((termsArrayNumeric.get(
                            position
                        ) * 24 * 60 * 60).toLong()) * 1000)
                        dueDateTimestamp = calendar.timeInMillis
                        val unused2 = dueDateTimestamp
                        dueText =
                            context?.let { MyConstants.formatDate(it, dueDateTimestamp, dateFormat) }
                        val unused3 = dueText
                        dueDate!!.setText(dueText)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (creationDateTimestamp > dueDateTimestamp) {
                    dueDate!!.setText(formatDate)
                    dueDateTimestamp = creationDateTimestamp
                    val unused4 = dueDateTimestamp
                }
            } else if (!dueDate!!.hasFocus()) {
            } else {
                if (instance.timeInMillis < creationDateTimestamp) {
                    showWarningDialog()
                    return
                }
                dueDate!!.setText(formatDate)
                dueDateTimestamp = instance.timeInMillis
                val unused5 = dueDateTimestamp
                terms!!.setText("KeyAttributes")
                position = 17
                val unused6 = position
            }
        }

        private fun showWarningDialog() {
            val builder = AlertDialog.Builder(
                (activity)!!
            )
            builder.setMessage("Due date must be after invoice date")
            builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity.DatePickerFragment.AnonymousClass1 */
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    dialogInterface.dismiss()
                }
            })
            builder.show()
        }
    }

    companion object {
        var calendar = Calendar.getInstance()
        private var creationDate: EditText? = null
        private var creationDateTimestamp: Long = 0
        private var dateFormat = 0
        private var dueDate: EditText? = null
        private var dueDateTimestamp: Long = 0
        private var dueText: String? = null
        private var position = 1
        private var terms: EditText? = null
        var termsArrayNumeric =
            intArrayOf(0, 0, 1, 2, 3, 4, 5, 6, 7, 10, 14, 30, 45, 60, 90, 180, 365, 0)

        @JvmStatic
        fun start(context: Context, catalogDTO2: CatalogDTO?) {
            val intent = Intent(context, InvoiceInfoActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, catalogDTO2)
            context.startActivity(intent)
        }
    }
}