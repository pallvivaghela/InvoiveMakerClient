package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.BusinessDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants

class PaymentOptionActivity : AppCompatActivity() {
    private var bankDetails: EditText? = null
    private var businessDTO: BusinessDTO? = null
    private var businessName: EditText? = null
    private var ivDone: ImageView? = null
    private var otherDetails: EditText? = null
    private var paypalAddress: EditText? = null
    private var toolbar: Toolbar? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_payment_option)
        intentData
        initLayout()
        if (businessDTO != null) {
            updatePaymentOption()
        }
        ivDone!!.setOnClickListener(`PaymentOptionActivity$$ExternalSyntheticLambda1`(this))
    }

    /* renamed from: lambda$onCreate$0$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-Activity-PaymentOptionActivity  reason: not valid java name */
    fun m27x19175934(view: View?) {
        onBackPressed()
    }

    private val intentData: Unit
        private get() {
            businessDTO = intent.getSerializableExtra(MyConstants.BUSINESS_DTO) as BusinessDTO?
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setTitle(R.string.payment_option)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        paypalAddress = findViewById<View>(R.id.paypal_address) as EditText
        ivDone = findViewById<View>(R.id.iv_done) as ImageView
        businessName = findViewById<View>(R.id.business_name) as EditText
        bankDetails = findViewById<View>(R.id.bank_details) as EditText
        otherDetails = findViewById<View>(R.id.other_details) as EditText
    }

    private fun updatePaymentOption() {
        paypalAddress!!.setText(businessDTO!!.paypalAddress)
        businessName!!.setText(businessDTO!!.checkInformation)
        bankDetails!!.setText(businessDTO!!.bankInformation)
        otherDetails!!.setText(businessDTO!!.otherPaymentInformation)
    }

    private fun updateOnBackPressed(): Boolean {
        val trim = paypalAddress!!.text.toString().trim { it <= ' ' }
        if (trim == "" || Patterns.EMAIL_ADDRESS.matcher(trim).matches()) {
            saveItem()
            return true
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.paypal_error_message))
        builder.setPositiveButton(
            R.string.ok,
            `PaymentOptionActivity$$ExternalSyntheticLambda0`.INSTANCE
        )
        builder.show()
        return false
    }


    override fun onBackPressed() {
        if (updateOnBackPressed()) {
            super.onBackPressed()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveItem() {
        if (businessDTO == null) {
            businessDTO = BusinessDTO()
        }
        businessDTO!!.paypalAddress = paypalAddress!!.text.toString()
        businessDTO!!.checkInformation = businessName!!.text.toString()
        businessDTO!!.bankInformation = bankDetails!!.text.toString()
        businessDTO!!.otherPaymentInformation = otherDetails!!.text.toString()
        if (businessDTO!!.id > 0) {
            LoadDatabase.instance!!.updateBusinessInformation(businessDTO!!)
        } else {
            LoadDatabase.instance!!.saveBusinessInformation(businessDTO!!)
        }
    }

    companion object {
        @JvmStatic
        fun `lambda$updateOnBackPressed$1`(dialogInterface: DialogInterface?, i: Int) {
        }

        @JvmStatic
        fun start(context: Context, businessDTO2: BusinessDTO?, j: Long) {
            val intent = Intent(context, PaymentOptionActivity::class.java)
            intent.putExtra(MyConstants.BUSINESS_DTO, businessDTO2)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            context.startActivity(intent)
        }
    }
}