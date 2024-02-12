package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.TaxDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants

class TaxActivity : AppCompatActivity() {
    private var catalogId: Long = 0
    private var ivDone: ImageView? = null
    private var spinner: Spinner? = null
    private var taxLabel: String? = ""
    private var taxLabelLayout: LinearLayout? = null
    private var taxRate = 0.0
    private var taxRateLayout: LinearLayout? = null
    private var taxType = 0
    private var tax_label: EditText? = null
    private var tax_rate: EditText? = null
    private var toolbar: Toolbar? = null
    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            taxType = intent.getIntExtra(MyConstants.TAX_TYPE, 0)
            taxRate = intent.getDoubleExtra(MyConstants.TAX_RATE, 0.0)
            taxLabel = intent.getStringExtra(MyConstants.TAX_LABEL)
        }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_tax)
        intentData
        initLayout()
        ivDone!!.setOnClickListener(`TaxActivity$$ExternalSyntheticLambda0`(this))
    }

    fun m31x72efbb44(view: View?) {
        onBackPressed()
    }
    private fun initLayout() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        ivDone = findViewById<View>(R.id.iv_done) as ImageView
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.taxes)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        taxLabelLayout = findViewById<View>(R.id.tax_label_layout) as LinearLayout
        taxRateLayout = findViewById<View>(R.id.tax_rate_layout) as LinearLayout
        tax_label = findViewById<View>(R.id.tax_label) as EditText
        tax_rate = findViewById<View>(R.id.tax_rate) as EditText
        tax_label!!.setText(taxLabel)
        val editText = tax_rate
        editText!!.setText("" + MyConstants.formatDecimal(java.lang.Double.valueOf(taxRate)))
        checkTaxType()
        val spinner2 = findViewById<View>(R.id.spinner) as Spinner
        spinner = spinner2
        spinner2.setSelection(taxType)
        spinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}

            @SuppressLint("WrongConstant")
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, j: Long) {
                taxType = i
                val i2 = taxType
                if (i2 == 0 || i2 == 1) {
                    taxRateLayout!!.visibility = 0
                } else {
                    taxRateLayout!!.visibility = 8
                }
            }
        })
    }

    @SuppressLint("WrongConstant")
    private fun checkTaxType(): Boolean {
        val i = taxType
        if (i != 0 && i != 1) {
            return false
        }
        taxRateLayout!!.visibility = 0
        return true
    }

    private fun updateOnBackPressed() {
        if (checkTaxType()) {
            try {
                taxRate = java.lang.String.valueOf(tax_rate!!.text.toString()).toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            taxRate = 0.0
        }
        if (catalogId == 0L) {
            catalogId = MyConstants.createNewInvoice()
        }
        val taxDTO = TaxDTO()
        taxDTO.catalogId = catalogId
        taxDTO.taxType = taxType
        taxDTO.taxLabel = tax_label!!.text.toString().trim { it <= ' ' }
        taxDTO.taxRate = taxRate
        LoadDatabase.instance?.updateInvoiceTax(taxDTO)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        updateOnBackPressed()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        @JvmStatic
        fun start(context: Context, j: Long, i: Int, str: String?, d: Double) {
            val intent = Intent(context, TaxActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.TAX_TYPE, i)
            intent.putExtra(MyConstants.TAX_LABEL, str)
            intent.putExtra(MyConstants.TAX_RATE, d)
            context.startActivity(intent)
        }
    }
}