package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddPaymentActivity.Companion.start
import android.app.Activity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.PaymentDTO
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import java.util.ArrayList

class PaymentAdapter(
    private val mActivity: Activity,
    private val paymentDTOS: ArrayList<PaymentDTO>
) : BaseAdapter() {
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    class MainPaymentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentAmount: TextView
        val paymentDate: TextView

        init {
            paymentDate = view.findViewById<View>(R.id.payment_date) as TextView
            paymentAmount = view.findViewById<View>(R.id.payment_amount) as TextView
        }
    }

    override fun getCount(): Int {
        return paymentDTOS.size
    }

    override fun getItem(i: Int): Any {
        return paymentDTOS[i]
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        if (view != null) {
            return view
        }
        val inflate =
            LayoutInflater.from(mActivity).inflate(R.layout.main_payment_layout, null as ViewGroup?)
        val mainPaymentHolder = MainPaymentHolder(inflate)
        mainPaymentHolder.paymentDate.text = MyConstants.formatDate(
            mActivity,
            paymentDTOS[i].paymentDate!!.toLong(),
            SettingsDTO.settingsDTO!!.dateFormat
        )
        mainPaymentHolder.paymentAmount.text = MyConstants.formatDecimal(
            java.lang.Double.valueOf(
                paymentDTOS[i].paidAmount
            )
        ).toString()
        inflate.tag = mainPaymentHolder
        inflate.setOnClickListener { view ->
            start(view.context, paymentDTOS[i].id, 1)
        }
        return inflate
    }
}