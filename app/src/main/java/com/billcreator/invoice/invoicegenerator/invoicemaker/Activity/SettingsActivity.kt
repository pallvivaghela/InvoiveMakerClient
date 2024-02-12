package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddLogoActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.BusinessDetailsActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.PaymentOptionActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.AppCore
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseHelper
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.BusinessDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.*
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
@SuppressWarnings("all")
class SettingsActivity() : AppCompatActivity(), View.OnClickListener, ModelChangeListener {
    private var businessDTO: BusinessDTO? = null
    var changePin: TextView? = null
    var currencyFormatValue: TextView? = null
    var dateFormatValue: TextView? = null
    var dbFILEPATH: String? = null
    var filename: String? = null
    var pinLockLayout: RelativeLayout? = null
    var pinLockSwitch: Switch? = null
    private var settingsDTO: SettingsDTO? = null
    var toolbar: Toolbar? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.settings_fragment_layout)
        DataProcessor.instance?.addChangeListener(this)
        initLayout()
    }


    public override fun onResume() {
        super.onResume()
        loadData()
        updateLayout()
    }

    private fun loadData() {
        businessDTO = LoadDatabase.instance?.businessInformation
        settingsDTO = SettingsDTO.settingsDTO
    }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.settings_text)
        currencyFormatValue = findViewById<View>(R.id.currency_format_value) as TextView
        dateFormatValue = findViewById<View>(R.id.date_format_value) as TextView
        findViewById<View>(R.id.select_logo).setOnClickListener(this)
        findViewById<View>(R.id.business_details).setOnClickListener(this)
        findViewById<View>(R.id.payment_options).setOnClickListener(this)
        findViewById<View>(R.id.currency_format_layout).setOnClickListener(this)
        findViewById<View>(R.id.date_format_layout).setOnClickListener(this)
        findViewById<View>(R.id.backup).setOnClickListener(this)
        findViewById<View>(R.id.restore).setOnClickListener(this)
        findViewById<View>(R.id.export).setOnClickListener(this)
        findViewById<View>(R.id.reset).setOnClickListener(this)
        managePinLayout()
    }

    @SuppressLint("ResourceType")
    private fun managePinLayout() {
        pinLockLayout = findViewById<View>(R.id.pin_lock_layout) as RelativeLayout
        pinLockSwitch = findViewById<View>(R.id.pin_lock_switch) as Switch
        changePin = findViewById<View>(R.id.change_pin) as TextView
        pinLockLayout!!.setOnClickListener(this)
        changePin!!.setOnClickListener(this)
        if (SessionManager.getInstance(
                applicationContext
            )?.passcode == -1
        ) {
            pinLockSwitch!!.isChecked = false
            changePin!!.setTextColor(resources.getColor(17170432))
            return
        }
        pinLockSwitch!!.isChecked = true
        changePin!!.setTextColor(resources.getColor(R.color.black))
    }

    private fun updateLayout() {
        currencyFormatValue!!.text = resources.getStringArray(R.array.currency_symbols).get(
            settingsDTO!!.currencyFormat
        )
        dateFormatValue!!.text = MyConstants.formatDate(
            this,
            Calendar.getInstance().timeInMillis,
            settingsDTO!!.dateFormat
        )
    }

    @SuppressLint("WrongConstant", "ResourceType")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.backup -> {
                backup()
                return
            }
            R.id.business_details -> {
                start(view.context, businessDTO)
                return
            }
            R.id.change_pin -> {
                if (pinLockSwitch!!.isChecked) {
                    val intent = Intent(this, LockScreen::class.java)
                    intent.putExtra("ScreenType", 1)
                    startActivity(intent)
                    return
                }
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.change_pin_message),
                    0
                ).show()
                return
            }
            R.id.currency_format_layout -> {
                showCurrencyDialog()
                return
            }
            R.id.date_format_layout -> {
                showDateDialog()
                return
            }
            R.id.export -> {
                export()
                return
            }
            R.id.payment_options -> {
                start(view.context, businessDTO, 0)
                return
            }
            R.id.pin_lock_layout -> {
                if (pinLockSwitch!!.isChecked) {
                    pinLockSwitch!!.isChecked = false
                    SessionManager.getInstance(
                        applicationContext
                    )?.passcode = -1
                    changePin!!.setTextColor(resources.getColor(17170432))
                    return
                }
                val intent2 = Intent(this, LockScreen::class.java)
                intent2.putExtra("ScreenType", 1)
                startActivity(intent2)
                return
            }
            R.id.reset -> {
                reset()
                return
            }
            R.id.restore -> {
                restore()
                return
            }
            R.id.select_logo -> {
                start(view.context, businessDTO!!.logo, businessDTO!!.id)
                return
            }
            else -> return
        }
    }

    private fun export() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.export)
        builder.setItems(arrayOf("Device", "Cloud")) { dialogInterface, i ->
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass1 */
            exportCSV(i)
        }
        builder.create()
        builder.show()
    }

    private fun exportCSV(i: Int) {
        if (i == 0) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) != 0
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    )
                ) {
                    Toast.makeText(this, "You have denied this permission.", Toast.LENGTH_LONG).show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                        101
                    )
                }
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) == 0
            ) {
                exportCSVOperation(1)
            }
        } else if (i == 1) {
            exportCSVOperation(2)
        }
    }

    private fun reset() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.reset)
        builder.setMessage(R.string.want_to_erase_all_the_data)
        builder.setPositiveButton(R.string.yes, object : DialogInterface.OnClickListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass2 */
            @SuppressLint("WrongConstant")
            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                val settingsActivity = this@SettingsActivity
                val show = ProgressDialog.show(
                    settingsActivity,
                    settingsActivity.getString(R.string.app_name),
                    this@SettingsActivity.getString(R.string.processing),
                    true
                )
                Handler().postDelayed(
                    Runnable
                    /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass2.AnonymousClass1 */
                    {
                        LoadDatabase.instance?.truncateDB(this@SettingsActivity.applicationContext)
                        (this@SettingsActivity.application as AppCore).init()
                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        intent.putExtra("from_app", true)
                        intent.addFlags(67141632)
                        this@SettingsActivity.startActivity(intent)
                        Toast.makeText(
                            this@SettingsActivity.applicationContext,
                            this@SettingsActivity.getString(R.string.all_data_reset),
                            0
                        ).show()
                        show.dismiss()
                    }, 1000
                )
            }
        })
        builder.setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i: Int) {}
        })
        builder.create()
        builder.show()
    }

    private fun restore() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Restore")
        builder.setItems(arrayOf("Device", "Cloud"), object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                importDb(i)
            }
        })
        builder.create()
        builder.show()
    }

    private fun importDb(i: Int) {
        if (i == 0) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) != 0
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    )
                ) {
                    Toast.makeText(this, "You have denied this permission.", Toast.LENGTH_LONG).show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                        102
                    )
                }
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) == 0
            ) {
                startActivityForResult(Intent(this, FilePickerActivity::class.java), 80)
            }
        } else if (i == 1) {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "application/x-sqlite3"
            startActivityForResult(Intent.createChooser(intent, "RESTORE"), 14)
        }
    }

    private fun backup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.backup)
        builder.setItems(arrayOf("Device"), object : DialogInterface.OnClickListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass5 */
            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                exportDb(i)
            }
        })
        builder.create()
        builder.show()
    }

    private fun exportDb(i: Int) {
        if (i == 0) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) != 0
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    )
                ) {
                    Toast.makeText(this, getString(R.string.denied_permission), Toast.LENGTH_LONG).show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                        101
                    )
                }
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) == 0
            ) {
                exportOperation(1)
            }
        } else if (i == 1) {
            exportOperation(2)
        }
    }

    fun exportOperation(i: Int) {
        val file: File?
        if (i == 2) {
            file = externalCacheDir
        } else {
            val file2 = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/Invoice/backup"
            )
            if (!file2.exists()) {
                file2.mkdir()
            }
            file = file2
        }
        val format = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(Date())
        if (i == 2) {
            filename = file.toString() + "/" + format + ".bak"
        } else {
            try {
                filename = file.toString() + "/" + format + ".bak"
                FileWriter(filename).close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val show = ProgressDialog.show(this, getString(R.string.app_name), "Processing...", true)
        object : Thread() {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass6 */
            override fun run() {
                try {
                    val settingsActivity = this@SettingsActivity
                    settingsActivity.dbFILEPATH =
                        settingsActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).toString()
                    if (DatabaseHelper(this@SettingsActivity).copyDbOperation(
                            dbFILEPATH,
                            filename
                        )
                    ) {
                        runOnUiThread(object : Runnable {
                            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass6.AnonymousClass1 */
                            @SuppressLint("WrongConstant")
                            override fun run() {
                                if (i == 1) {
                                    Toast.makeText(
                                        this@SettingsActivity,
                                        this@SettingsActivity.getString(R.string.backup_created),
                                        0
                                    ).show()
                                } else if (i == 2) {
                                    val intent = Intent("android.intent.action.SEND")
                                    intent.type = "application/x-sqlite3"
                                    val settingsActivity = this@SettingsActivity
                                    intent.putExtra(
                                        "android.intent.extra.STREAM", FileProvider.getUriForFile(
                                            settingsActivity,
                                            this@SettingsActivity.applicationContext.packageName + ".my.package.name.provider",
                                            File(
                                                filename
                                            )
                                        )
                                    )
                                    intent.putExtra(
                                        "android.intent.extra.SUBJECT",
                                        "Invoice And Estimate"
                                    )
                                    intent.flags = 1
                                    this@SettingsActivity.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "BACKUP"
                                        )
                                    )
                                }
                            }
                        })
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
        timerDelayRemoveDialog(1000, show)
    }

    private fun exportCSVOperation(i: Int) {
        val file: File?
        if (i == 2) {
            file = externalCacheDir
        } else {
            val file2 = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/Invoice/Export"
            )
            if (!file2.exists()) {
                file2.mkdir()
            }
            file = file2
        }
        val format = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(Date())
        if (i == 2) {
            filename = file.toString() + "/" + format + ".csv"
        } else {
            try {
                filename = file.toString() + "/" + format + ".csv"
                FileWriter(filename).close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val show = ProgressDialog.show(this, getString(R.string.app_name), "Proccessing...", true)
        object : Thread() {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass7 */
            override fun run() {
                try {
                    val settingsActivity = this@SettingsActivity
                    settingsActivity.createCSVFile(settingsActivity.filename)
                    runOnUiThread(object : Runnable {
                        /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass7.AnonymousClass1 */
                        @SuppressLint("WrongConstant")
                        override fun run() {
                            if (i == 1) {
                                Toast.makeText(
                                    this@SettingsActivity,
                                    "CSV file is created to Export Folder",
                                    0
                                ).show()
                            } else if (i == 2) {
                                val intent = Intent("android.intent.action.SEND")
                                intent.type = "message/rfc822"
                                intent.putExtra(
                                    "android.intent.extra.SUBJECT",
                                    "Invoice And Estimate Maker"
                                )
                                val settingsActivity = this@SettingsActivity
                                intent.putExtra(
                                    "android.intent.extra.STREAM", FileProvider.getUriForFile(
                                        settingsActivity,
                                        this@SettingsActivity.packageName + ".my.package.name.provider",
                                        File(
                                            filename
                                        )
                                    )
                                )
                                intent.flags = 1
                                this@SettingsActivity.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "Select Application."
                                    )
                                )
                            }
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
        timerDelayRemoveDialog(1000, show)
    }

    private fun createCSVFile(str: String?) {
        object : Thread() {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass8 */
            override fun run() {
                try {
                    val fileWriter = FileWriter(str)
                    val allInvoices = LoadDatabase.instance?.allInvoices
                    fileWriter.append("Invoice" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Date" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Due" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Client" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Client Email" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Tax" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Discount" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Shipping" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Subtotal" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Total" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Paid" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("Balance due" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("PO #" as CharSequence)
                    fileWriter.append("," as CharSequence)
                    fileWriter.append("10" as CharSequence)
                    for (i in allInvoices!!.indices) {
                        val calculateInvoiceBalance =
                            LoadDatabase.instance?.calculateInvoiceBalance(
                                allInvoices[i].id
                            )
                        fileWriter.append(calculateInvoiceBalance?.catalogName as CharSequence)
                        fileWriter.append("," as CharSequence)
                        fileWriter.append(MyConstants.sdf.format(Date(calculateInvoiceBalance.creationDate!!.toLong())) as CharSequence)
                        fileWriter.append("," as CharSequence)
                        if (calculateInvoiceBalance.dueDate != null && calculateInvoiceBalance.dueDate != "") {
                            fileWriter.append(MyConstants.sdf.format(Date(calculateInvoiceBalance.dueDate!!.toLong())) as CharSequence)
                        }
                        fileWriter.append("," as CharSequence)
                        if (calculateInvoiceBalance.clientDTO.clientName != null) {
                            fileWriter.append(calculateInvoiceBalance.clientDTO.clientName as CharSequence)
                        }
                        fileWriter.append("," as CharSequence)
                        if (calculateInvoiceBalance.clientDTO.emailAddress != null) {
                            fileWriter.append(calculateInvoiceBalance.clientDTO.emailAddress as CharSequence)
                        }
                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(
                                    calculateInvoiceBalance.taxAmount

                            ).toString() + "") as CharSequence
                        )
                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(
                                    calculateInvoiceBalance.discountAmount

                            ).toString() + "") as CharSequence
                        )
                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(

                                    calculateInvoiceBalance.invoiceShippingDTO.amount
                                )
                            ).toString() + "") as CharSequence

                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(

                                    calculateInvoiceBalance.subTotalAmount
                                )
                            ).toString() + "") as CharSequence

                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(

                                    calculateInvoiceBalance.totalAmount.toDouble()
                                )
                            ).toString() + "") as CharSequence

                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(

                                    calculateInvoiceBalance.paidAmount.toDouble()
                                )
                            ).toString() + "") as CharSequence

                        fileWriter.append("," as CharSequence)
                        fileWriter.append(
                            (MyConstants.formatDecimal(

                                    calculateInvoiceBalance.totalAmount - calculateInvoiceBalance.paidAmount.toDouble()
                                )
                            ).toString() + "") as CharSequence

                        fileWriter.append("," as CharSequence)
                        if (calculateInvoiceBalance.poNumber != null) {
                            fileWriter.append(calculateInvoiceBalance.poNumber as CharSequence)
                        }
                        fileWriter.append("," as CharSequence)
                        fileWriter.append("10" as CharSequence)
                    }
                    fileWriter.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun timerDelayRemoveDialog(j: Long, dialog: Dialog) {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                dialog.dismiss()
            }
        }, j)
    }

    fun showCurrencyDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setSingleChoiceItems(
            resources.getStringArray(R.array.currency_format_titles),
            settingsDTO!!.currencyFormat,
            object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    settingsDTO!!.currencyFormat = i
                    LoadDatabase.instance?.updateSettings(settingsDTO!!)
                    dialogInterface.dismiss()
                }
            })
        builder.create().show()
    }

    private fun showDateDialog() {
        val builder = AlertDialog.Builder(this)
        val length = resources.getStringArray(R.array.date_format_titles).size
        val strArr = arrayOfNulls<String>(length)
        val timeInMillis = Calendar.getInstance().timeInMillis
        for (i in 0 until length) {
            strArr[i] = MyConstants.formatDate(this, timeInMillis, i)
        }
        builder.setSingleChoiceItems(
            strArr,
            settingsDTO!!.dateFormat,
            object : DialogInterface.OnClickListener {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity.AnonymousClass11 */
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    settingsDTO!!.dateFormat = i
                    LoadDatabase.instance?.updateSettings(settingsDTO!!)
                    dialogInterface.dismiss()
                }
            })
        builder.create().show()
    }

    override fun onReceiveModelChange(str: String?, i: Int) {
        val gson = Gson()
        if (i == 321) {
            settingsDTO = gson.fromJson(str, SettingsDTO::class.java) as SettingsDTO
            updateLayout()
        } else if (i == 666) {
            managePinLayout()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        DataProcessor.instance?.removeChangeListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val REQUEST_CODE_RESTORE = 14
        val REQUEST_FILE_PICKER = 80
        val TAG = "SettingsActivity"
    }
}