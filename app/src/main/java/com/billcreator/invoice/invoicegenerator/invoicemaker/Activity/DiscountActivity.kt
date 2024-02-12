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
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.DiscountDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants

class DiscountActivity : AppCompatActivity() {
    private var catalogId: Long = 0
    private var discountAmount = 0.0
    private var discountLayout: LinearLayout? = null
    private var discountText: TextView? = null
    private var discountType = 0
    private var etDiscountAmount: EditText? = null
    private var icDone: ImageView? = null
    private var spinner: Spinner? = null
    private var subtotalAmount = 0.0
    private var toolbar: Toolbar? = null
    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            discountType = intent.getIntExtra(MyConstants.DISCOUNT_TYPE, 0)
            discountAmount = intent.getDoubleExtra(MyConstants.DISCOUNT_AMOUNT_TOTAL, 0.0)
            subtotalAmount = intent.getDoubleExtra(MyConstants.DISCOUNT_AMOUNT_SUBTOTAL, 0.0)
        }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_discount)
        intentData
        initLayout()
        icDone!!.setOnClickListener(`DiscountActivity$$ExternalSyntheticLambda0`(this))
    }

    /* renamed from: lambda$onCreate$0$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-Activity-DiscountActivity  reason: not valid java name */
    fun m25x5fa28e46(view: View?) {
        onBackPressed()
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        icDone = findViewById<View>(R.id.iv_done) as ImageView
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.discount)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        discountLayout = findViewById<View>(R.id.discount_layout) as LinearLayout
        val editText = findViewById<View>(R.id.discount_amount) as EditText
        etDiscountAmount = editText
        if (discountType == 2) {
            editText.setText("" + MyConstants.formatDecimal(java.lang.Double.valueOf(discountAmount * 100.0 / subtotalAmount)))
        } else {
            editText.setText("" + MyConstants.formatDecimal(java.lang.Double.valueOf(discountAmount)))
        }
        val textView = findViewById<View>(R.id.discount_text) as TextView
        discountText = textView
        val i = discountType
        if (i == 2) {
            textView.text = "Discount (%)"
            discountLayout!!.visibility = 0
        } else if (i != 3) {
            discountLayout!!.visibility = 8
        } else {
            textView.text = "Amount"
            discountLayout!!.visibility = 0
        }
        val spinner2 = findViewById<View>(R.id.spinner) as Spinner
        spinner = spinner2
        spinner2.setSelection(discountType)
        spinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.DiscountActivity.AnonymousClass1 */

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}


            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, j: Long) {
                discountType = i
                val i2 = discountType
                if (i2 == 2) {
                    discountText!!.text = "Discount (%)"
                    discountLayout!!.visibility = 0
                } else if (i2 != 3) {
                    discountLayout!!.visibility = 8
                } else {
                    discountText!!.text = "Amount"
                    discountLayout!!.visibility = 0
                }
            }
        })
    }

    private fun updateOnBackPressed() {
        val i = discountType
        if (i == 3 || i == 2) {
            try {
                discountAmount =
                    java.lang.Double.valueOf(etDiscountAmount!!.text.toString()).toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            discountAmount = 0.0
        }
        if (catalogId == 0L) {
            catalogId = MyConstants.createNewInvoice()
        }
        val discountDTO = DiscountDTO()
        discountDTO.discountType = discountType
        discountDTO.discountAmount = discountAmount
        discountDTO.catalogId = catalogId
        LoadDatabase.instance!!.updateInvoiceDiscount(discountDTO)
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
        fun start(context: Context, j: Long, i: Int, d: Double, d2: Double) {
            val intent = Intent(context, DiscountActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.DISCOUNT_TYPE, i)
            intent.putExtra(MyConstants.DISCOUNT_AMOUNT_TOTAL, d)
            intent.putExtra(MyConstants.DISCOUNT_AMOUNT_SUBTOTAL, d2)
            context.startActivity(intent)
        }
    }
}