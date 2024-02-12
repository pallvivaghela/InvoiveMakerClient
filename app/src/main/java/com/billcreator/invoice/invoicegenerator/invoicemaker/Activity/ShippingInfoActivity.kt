package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.InvoiceShippingDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import java.util.*

class ShippingInfoActivity : AppCompatActivity(), View.OnClickListener {
    private var catalogId: Long = 0
    private var fob: EditText? = null
    private var ivDone: ImageView? = null
    private var shipVia: EditText? = null
    private var shippingAmount: EditText? = null
    private var shippingDTO: InvoiceShippingDTO? = null
    private var toolbar: Toolbar? = null
    private var trackingNumber: EditText? = null
    override fun onClick(view: View) {}
    class DatePickerFragment : DialogFragment(), OnDateSetListener {

        @SuppressLint("UseRequireInsteadOfGet")
        override fun onCreateDialog(bundle: Bundle?): Dialog {
            val instance = Calendar.getInstance()
            return DatePickerDialog(activity!!, this, instance[1], instance[2], instance[5])
        }

        override fun onDateSet(datePicker: DatePicker, i: Int, i2: Int, i3: Int) {
            shipDate!!.setText(
                String.format(
                    "%d/%02d/%02d",
                    Integer.valueOf(i),
                    Integer.valueOf(i2 + 1),
                    Integer.valueOf(i3)
                )
            )
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_shipping_info)
        intentData
        initLayout()
        ivDone!!.setOnClickListener(`ShippingInfoActivity$$ExternalSyntheticLambda1`(this))
    }

    /* renamed from: lambda$onCreate$0$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-Activity-ShippingInfoActivity  reason: not valid java name */
    fun m29x532f8281(view: View?) {
        onBackPressed()
    }

    private val intentData: Unit
        private get() {
            shippingDTO =

                intent.getSerializableExtra(MyConstants.SHIPPING_DTO) as InvoiceShippingDTO?
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setTitle(R.string.shipping)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        shippingAmount = findViewById<View>(R.id.shipping_amount) as EditText
        shipDate = findViewById<View>(R.id.ship_date) as EditText
        shipVia = findViewById<View>(R.id.ship_via) as EditText
        trackingNumber = findViewById<View>(R.id.tracking_number) as EditText
        ivDone = findViewById<View>(R.id.iv_done) as ImageView
        fob = findViewById<View>(R.id.fob) as EditText
        shipDate!!.inputType = 0
        shipDate!!.inputType = 0
        shipDate!!.setOnClickListener(`ShippingInfoActivity$$ExternalSyntheticLambda0`(this))
        if (shippingDTO!!.id > 0) {
            updateShippingInfo()
        }
    }

    fun m28xed6ace1(view: View?) {
        showDatePickerDialog()
    }

    private fun updateShippingInfo() {
        val editText = shippingAmount
        editText?.setText(
            MyConstants.formatDecimal(java.lang.Double.valueOf(shippingDTO!!.amount))
                .toString() + ""
        )
        shipDate!!.setText(shippingDTO!!.shippingDate)
        shipVia!!.setText(shippingDTO!!.shipVia)
        trackingNumber!!.setText(shippingDTO!!.tracking)
        fob!!.setText(shippingDTO!!.fob)
    }

    fun showDatePickerDialog() {
        DatePickerFragment().show(
            supportFragmentManager, "datePicker"
        )
    }

    private fun updateOnBackPressed() {
        val d: Double
        d = try {
            shippingAmount!!.text.toString().toDouble()
        } catch (unused: Exception) {
            0.0
        }
        shippingDTO!!.amount = d
        shippingDTO!!.shippingDate = shipDate!!.text.toString()
        shippingDTO!!.shipVia = shipVia!!.text.toString()
        shippingDTO!!.tracking = trackingNumber!!.text.toString()
        shippingDTO!!.fob = fob!!.text.toString()
        if (catalogId == 0L) {
            catalogId = MyConstants.createNewInvoice()
        }
        shippingDTO!!.catalogId = catalogId
        if (shippingDTO!!.id > 0) {
            LoadDatabase.instance?.updateInvoiceShipping(shippingDTO!!)
        } else {
            LoadDatabase.instance?.saveInvoiceShipping(shippingDTO!!)
        }
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
        var calendar = Calendar.getInstance()
        private var shipDate: EditText? = null

        @JvmStatic
        fun start(context: Context, invoiceShippingDTO: InvoiceShippingDTO?, j: Long) {
            val intent = Intent(context, ShippingInfoActivity::class.java)
            intent.putExtra(MyConstants.SHIPPING_DTO, invoiceShippingDTO)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            context.startActivity(intent)
        }
    }
}