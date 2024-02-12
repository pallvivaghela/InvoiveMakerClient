package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.BusinessDTO
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar

class BusinessDetailsActivity : AppCompatActivity() {
    private var addressLine1: EditText? = null
    private var businessDTO: BusinessDTO? = null
    private var businessName: EditText? = null
    private var emailAddress: EditText? = null
    private var faxNo: EditText? = null
    private var ivDone: ImageView? = null
    private var mobileNo: EditText? = null
    private var phoneNo: EditText? = null
    private var regNo: EditText? = null
    private var toolbar: Toolbar? = null
    private var website: EditText? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_business_details)
        intentData
        initLayout()
        if (businessDTO != null) {
            updateLayout()
        }
        ivDone!!.setOnClickListener { onBackPressed() }
    }

    private val intentData: Unit
        private get() {
            businessDTO = intent.getSerializableExtra(MyConstants.BUSINESS_DTO) as BusinessDTO?
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Business Details"
        businessName = findViewById<View>(R.id.business_name) as EditText
        regNo = findViewById<View>(R.id.reg_no) as EditText
        addressLine1 = findViewById<View>(R.id.address_line1) as EditText
        phoneNo = findViewById<View>(R.id.phone_no) as EditText
        mobileNo = findViewById<View>(R.id.mobile_no) as EditText
        faxNo = findViewById<View>(R.id.fax_no) as EditText
        emailAddress = findViewById<View>(R.id.email_address) as EditText
        website = findViewById<View>(R.id.website) as EditText
        ivDone = findViewById<View>(R.id.iv_done) as ImageView
    }

    private fun updateLayout() {
        businessName!!.setText(businessDTO!!.name)
        regNo!!.setText(businessDTO!!.regNo)
        addressLine1!!.setText(businessDTO!!.line1)
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
        businessDTO!!.name = businessName!!.text.toString()
        businessDTO!!.regNo = regNo!!.text.toString()
        businessDTO!!.line1 = addressLine1!!.text.toString()
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
        @JvmStatic
        fun start(context: Context, businessDTO2: BusinessDTO?) {
            val intent = Intent(context, BusinessDetailsActivity::class.java)
            intent.putExtra(MyConstants.BUSINESS_DTO, businessDTO2)
            context.startActivity(intent)
        }
    }
}