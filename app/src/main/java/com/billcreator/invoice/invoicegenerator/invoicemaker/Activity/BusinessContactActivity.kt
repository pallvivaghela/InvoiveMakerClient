package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.BusinessDTO
import android.widget.EditText
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.Toolbar

class BusinessContactActivity : AppCompatActivity() {
    private var businessDTO: BusinessDTO? = null
    private var emailAddress: EditText? = null
    private var faxNo: EditText? = null
    private var mobileNo: EditText? = null
    private var phoneNo: EditText? = null
    private var toolbar: Toolbar? = null
    private var website: EditText? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_business_contact)
        intentData
        initLayout()
        if (businessDTO != null) {
            updatePaymentOption()
        }
    }

    private val intentData: Unit
        private get() {
            businessDTO = intent.getSerializableExtra(MyConstants.BUSINESS_DTO) as BusinessDTO?
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.title = "Business Contact"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        phoneNo = findViewById<View>(R.id.phone_no) as EditText
        mobileNo = findViewById<View>(R.id.mobile_no) as EditText
        faxNo = findViewById<View>(R.id.fax_no) as EditText
        emailAddress = findViewById<View>(R.id.email_address) as EditText
        website = findViewById<View>(R.id.website) as EditText
    }

    private fun updatePaymentOption() {
        phoneNo!!.setText(businessDTO!!.phoneNo)
        mobileNo!!.setText(businessDTO!!.mobileNo)
        faxNo!!.setText(businessDTO!!.fax)
        emailAddress!!.setText(businessDTO!!.email)
        website!!.setText(businessDTO!!.website)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        saveItem()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveItem() {
        if (businessDTO == null) {
            businessDTO = BusinessDTO()
        }
        businessDTO!!.phoneNo = phoneNo!!.text.toString()
        businessDTO!!.mobileNo = mobileNo!!.text.toString()
        businessDTO!!.fax = faxNo!!.text.toString()
        businessDTO!!.email = emailAddress!!.text.toString()
        businessDTO!!.website = website!!.text.toString()
        if (businessDTO!!.id > 0) {
            LoadDatabase.instance!!.updateBusinessInformation(businessDTO!!)
        } else {
            LoadDatabase.instance!!.saveBusinessInformation(businessDTO!!)
        }
    }

    companion object {
        fun start(context: Context, businessDTO2: BusinessDTO?) {
            val intent = Intent(context, BusinessContactActivity::class.java)
            intent.putExtra(MyConstants.BUSINESS_DTO, businessDTO2)
            context.startActivity(intent)
        }
    }
}