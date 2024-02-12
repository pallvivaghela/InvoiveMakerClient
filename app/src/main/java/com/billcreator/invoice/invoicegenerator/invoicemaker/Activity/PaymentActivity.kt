package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.PaymentAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.PaymentDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.NonScrollListView

class PaymentActivity : AppCompatActivity(), View.OnClickListener, ModelChangeListener {
    private var allPayments: NonScrollListView? = null
    private var catalogId: Long = 0
    private var dueAmount: TextView? = null
    private var ivDone: ImageView? = null
    private var paidAmount = 0.0
    private var paidAmount1: TextView? = null
    private var paymentAdapter: PaymentAdapter? = null
    private var paymentDTOS: ArrayList<PaymentDTO>? = null
    private var toolbar: Toolbar? = null
    private var totalAmount = 0.0
    private var totalAmount1: TextView? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_payment)
        paymentDTOS = ArrayList()
        DataProcessor.instance!!.addChangeListener(this)
        intentData
        initLayout()
        if (catalogId > 0) {
            updateLayout()
        }
        ivDone!!.setOnClickListener(`PaymentActivity$$ExternalSyntheticLambda0`(this))
    }

    /* renamed from: lambda$onCreate$0$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-Activity-PaymentActivity  reason: not valid java name */
    fun m26x9b49dd9f(view: View?) {
        onBackPressed()
    }

    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            paidAmount = intent.getDoubleExtra(MyConstants.PAID_AMOUNT, 0.0)
            totalAmount = intent.getDoubleExtra(MyConstants.TOTAL_AMOUNT, 0.0)
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setTitle(R.string.payments)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        totalAmount1 = findViewById<View>(R.id.total_amount) as TextView
        paidAmount1 = findViewById<View>(R.id.paid_amount) as TextView
        dueAmount = findViewById<View>(R.id.due_amount) as TextView
        ivDone = findViewById<View>(R.id.iv_done) as ImageView
        loadPayments()
        allPayments = findViewById<View>(R.id.all_payments_list) as NonScrollListView
        val paymentAdapter2 = paymentDTOS?.let { PaymentAdapter(this, it) }
        paymentAdapter = paymentAdapter2
        allPayments!!.adapter = paymentAdapter2
        findViewById<View>(R.id.add_payment_fab).setOnClickListener(this)
    }

    private fun updateLayout() {
        val textView = totalAmount1
        textView!!.text = "$" + MyConstants.formatDecimal(totalAmount)
        val textView2 = paidAmount1
        textView2!!.text = "$" + MyConstants.formatDecimal(paidAmount)
        val textView3 = dueAmount
        textView3!!.text =
            "$" + MyConstants.formatDecimal(totalAmount - paidAmount)
    }

    private fun loadPayments() {
        paymentDTOS!!.clear()
        paymentDTOS = LoadDatabase.instance?.getPayments(catalogId)
    }

    private fun calculateTotalPaid() {
        paidAmount = 0.0
        for (i in paymentDTOS!!.indices) {
            paidAmount += paymentDTOS!![i].paidAmount
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.add_payment_fab) {
            start(
                view.context, catalogId, MyConstants.formatDecimal(

                        totalAmount - paidAmount

                ).toInt(), 0
            )
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

//    override fun onReceiveModelChange(str: String?, i: Int) {
//        TODO("Not yet implemented")
//    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
    override fun onReceiveModelChange(str: String?, i: Int) {
        if (i == 104) {
            try {
                loadPayments()
                calculateTotalPaid()
                val paymentAdapter2 = paymentDTOS?.let { PaymentAdapter(this, it) }
                paymentAdapter = paymentAdapter2
                allPayments!!.adapter = paymentAdapter2
                val textView = paidAmount1
                textView?.setText(
                    MyConstants.formatDecimal(paidAmount).toString() + ""
                )
                val textView2 = dueAmount
                textView2?.setText(
                    MyConstants.formatDecimal(totalAmount - paidAmount)
                        .toString() + ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, j: Long, d: Int, d2: Int) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.PAID_AMOUNT, d)
            intent.putExtra(MyConstants.TOTAL_AMOUNT, d2)
            context.startActivity(intent)
        }
    }


}