package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientDTO
import android.widget.EditText
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import android.os.Build
import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import java.lang.Exception

class AddClientActivity : AppCompatActivity(), View.OnClickListener {
    var btnDdelete: TextView? = null
    var btnImport: TextView? = null
    var btnReport: TextView? = null
    var btnSave: TextView? = null
    var clientDTO: ClientDTO? = null
    var clientName: EditText? = null
    var contactAddress: EditText? = null
    var emailAddress: EditText? = null
    var faxNo: EditText? = null
    var invoiceId: Long = 0
    var isInvoice = false
    var mTAG = "AddClientActivity"
    var mobileNo: EditText? = null
    var phoneNo: EditText? = null
    var searchItem: ImageView? = null
    var shippingAddressLine1: EditText? = null
    var shippingAddressName: EditText? = null
    var toolbar: Toolbar? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_add_client)
        intentData
        initLayout()
    }

    private val intentData: Unit
        private get() {
            clientDTO = intent.getSerializableExtra(MyConstants.CLIENT_DTO) as ClientDTO?
            invoiceId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            isInvoice = intent.getBooleanExtra(MyConstants.IS_INVOICE, false)
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (clientDTO!!.id == 0L) {
            supportActionBar!!.title = "New Client"
        } else {
            supportActionBar!!.title = "Edit Client"
        }
        val imageView = findViewById<View>(R.id.search_item) as ImageView
        searchItem = imageView
        imageView.setOnClickListener(this)
        clientName = findViewById<View>(R.id.client_name) as EditText
        emailAddress = findViewById<View>(R.id.email_address) as EditText
        mobileNo = findViewById<View>(R.id.mobile_no) as EditText
        phoneNo = findViewById<View>(R.id.phone_no) as EditText
        faxNo = findViewById<View>(R.id.fax_no) as EditText
        contactAddress = findViewById<View>(R.id.contact_address) as EditText
        shippingAddressName = findViewById<View>(R.id.shipping_address_name) as EditText
        shippingAddressLine1 = findViewById<View>(R.id.shipping_address_line1) as EditText
        if (clientDTO!!.id > 0) {
            updateClientInfo()
        } else {
            searchItem!!.visibility = 0
        }
        btnImport = findViewById<View>(R.id.import_from_contact) as TextView
        btnDdelete = findViewById<View>(R.id.delete_client) as TextView
        btnReport = findViewById<View>(R.id.client_report) as TextView
        val textView = findViewById<View>(R.id.save_client) as TextView
        btnSave = textView
        textView.setOnClickListener(this)
        btnDdelete!!.setOnClickListener(this)
        btnImport!!.setOnClickListener(this)
        btnReport!!.setOnClickListener(this)
        if (clientDTO!!.id != 0L) {
            btnReport!!.visibility = 0
            btnDdelete!!.visibility = 0
            btnImport!!.visibility = 8
        }
    }

    private fun updateClientInfo() {
        clientName!!.setText(clientDTO!!.clientName)
        emailAddress!!.setText(clientDTO!!.emailAddress)
        mobileNo!!.setText(clientDTO!!.mobileNo)
        phoneNo!!.setText(clientDTO!!.phoneNo)
        faxNo!!.setText(clientDTO!!.faxNo)
        contactAddress!!.setText(clientDTO!!.contactAdress)
        shippingAddressName!!.setText(clientDTO!!.shippingAddress)
        shippingAddressLine1!!.setText(clientDTO!!.shippingLine1)
    }

    private fun checkParmission() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            updateFromContact()
        } else {
            requestPermissions(arrayOf("android.permission.READ_CONTACTS"), 100)
        }
    }

    private fun updateFromContact() {
        startActivityForResult(
            Intent(
                "android.intent.action.PICK",
                ContactsContract.Contacts.CONTENT_URI
            ), mSELECTCONTACT
        )
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        if (i == 100) {
            if (iArr[0] == 0) {
                updateFromContact()
            } else {
                Toast.makeText(
                    this,
                    "Until you grant the permission, we cannot access contact information",
                    0
                ).show()
            }
        }
    }

    private fun saveClient() {
        val obj = clientName!!.text.toString()
        clientName!!.requestFocus()
        if (obj.trim { it <= ' ' }.equals("", ignoreCase = true)) {
            clientName!!.error = "Cannot be empty"
            return
        }
        clientDTO!!.clientName = clientName!!.text.toString().trim { it <= ' ' }
        clientDTO!!.emailAddress = emailAddress!!.text.toString().trim { it <= ' ' }
        clientDTO!!.mobileNo = mobileNo!!.text.toString().trim { it <= ' ' }
        clientDTO!!.phoneNo = phoneNo!!.text.toString().trim { it <= ' ' }
        clientDTO!!.faxNo = faxNo!!.text.toString().trim { it <= ' ' }
        clientDTO!!.contactAdress = contactAddress!!.text.toString().trim { it <= ' ' }
        clientDTO!!.shippingAddress = shippingAddressName!!.text.toString().trim { it <= ' ' }
        clientDTO!!.shippingLine1 = shippingAddressLine1!!.text.toString().trim { it <= ' ' }
        if (invoiceId == 0L && isInvoice) {
            invoiceId = MyConstants.createNewInvoice()
        }
        clientDTO!!.catalogId = invoiceId
        if (clientDTO!!.id <= 0) {
            clientDTO!!.id = LoadDatabase.instance!!.saveClient(clientDTO!!)
            LoadDatabase.instance!!.addInvoiceClient(clientDTO!!)
        } else if (LoadDatabase.instance!!.updateClient(clientDTO!!) > 0) {
            LoadDatabase.instance!!.addInvoiceClient(clientDTO!!)
        }
        finish()
    }

    private fun messageForDiscardChanges() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.discard_changes_message))
        builder.setPositiveButton("YES") { dialogInterface, i ->
            finish()
        }
        builder.setNegativeButton("NO") /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddClientActivity.AnonymousClass2 */{ dialogInterface, i -> }
        builder.create()
        builder.show()
    }


    override fun onBackPressed() {
        messageForDiscardChanges()
    }


    override fun onSupportNavigateUp(): Boolean {
        messageForDiscardChanges()
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.client_report -> {
                try {
                    ClientStatementPreviewActivity()
                    ClientStatementPreviewActivity.start(this, clientDTO)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
                LoadDatabase.instance!!.deleteInvoiceClient(invoiceId, 0)
                LoadDatabase.instance!!.deleteInvoiceShipping(invoiceId)
                LoadDatabase.instance!!.deleteClient(clientDTO!!.id)
                finish()
                return
            }
            R.id.delete_client -> {
                LoadDatabase.instance!!.deleteInvoiceClient(invoiceId, 0)
                LoadDatabase.instance!!.deleteInvoiceShipping(invoiceId)
                LoadDatabase.instance!!.deleteClient(clientDTO!!.id)
                finish()
                return
            }
            R.id.import_from_contact -> {
                checkParmission()
                return
            }
            R.id.save_client -> {
                saveClient()
                return
            }
            R.id.search_item -> {
                SearchClientActivity.start(view.context, invoiceId, false)
                return
            }
            else -> return
        }
    }


    @SuppressLint("Range")
    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        var str: String
        super.onActivityResult(i, i2, intent)
        if (i == mSELECTCONTACT && i2 == -1) {
            try {
                val query = contentResolver.query(intent!!.data!!, null, null, null, null)
                if (query!!.moveToFirst()) {
                    val string = query.getString(query.getColumnIndex("_id"))
                    clientName!!.setText(query.getString(query.getColumnIndex("display_name")))
                    val query2 = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "contact_id = ?",
                        arrayOf(string),
                        null
                    )
                    while (query2!!.moveToNext()) {
                        val string2 = query2.getString(query2.getColumnIndex("data1"))
                        val i3 = query2.getInt(query2.getColumnIndex("data2"))
                        if (i3 != 2) {
                            if (i3 != 12) {
                                if (i3 != 17 && (i3 == 4 || i3 == 5) && faxNo!!.text.toString() == "") {
                                    faxNo!!.setText(string2)
                                }
                            } else if (phoneNo!!.text.toString() == "") {
                                phoneNo!!.setText(string2)
                            }
                        }
                        if (mobileNo!!.text.toString() == "") {
                            mobileNo!!.setText(string2)
                        }
                    }
                    query2.close()
                    val query3 = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        "contact_id = ?",
                        arrayOf(string),
                        null
                    )
                    if (query3!!.moveToFirst()) {
                        emailAddress!!.setText(query3.getString(query3.getColumnIndex("data1")))
                    }
                    query3.close()
                    val uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI
                    val query4 = contentResolver.query(uri, null, "contact_id=$string", null, null)
                    while (query4!!.moveToNext()) {
                        val string3 = query4.getString(query4.getColumnIndex("data4"))
                        val string4 = query4.getString(query4.getColumnIndex("data7"))
                        val string5 = query4.getString(query4.getColumnIndex("data10"))
                        str = if (string3 != null) {
                            ("" as CharSequence).toString() + string3 + " "
                        } else {
                            ""
                        }
                        if (string4 != null) {
                            str = (str as CharSequence).toString() + string4 + " "
                        }
                        if (string5 != null) {
                            str = (str as CharSequence).toString() + string5 + " "
                        }
                        contactAddress!!.setText(str)
                    }
                    query4.close()
                }
                query.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        var mSELECTCONTACT = 987
        @SuppressLint("WrongConstant")
        @JvmStatic
        fun start(context: Context, clientDTO2: ClientDTO?, j: Long, z: Boolean) {
            val intent = Intent(context, AddClientActivity::class.java)
            intent.flags = 67108864
            intent.putExtra(MyConstants.CLIENT_DTO, clientDTO2)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.IS_INVOICE, z)
            context.startActivity(intent)
        }
    }
}