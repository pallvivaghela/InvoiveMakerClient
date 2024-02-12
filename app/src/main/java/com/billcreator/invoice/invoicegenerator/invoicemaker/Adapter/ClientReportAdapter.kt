package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientReportDTO
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

class ClientReportAdapter(
    private val mActivity: Activity,
    private val clientReportDTOS: ArrayList<ClientReportDTO>
) : BaseAdapter() {
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("WrongConstant")
    class ClientReportHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clientName: TextView
        private val clientsNumber: TextView
        val invocieNumber: TextView
        val paidAmount: TextView

        init {
            clientName = view.findViewById<View>(R.id.month_name) as TextView
            val textView = view.findViewById<View>(R.id.clients_number) as TextView
            clientsNumber = textView
            invocieNumber = view.findViewById<View>(R.id.invoice_number) as TextView
            paidAmount = view.findViewById<View>(R.id.paid_amount) as TextView
            textView.visibility = 4
        }
    }

    override fun getCount(): Int {
        return clientReportDTOS.size
    }

    override fun getItem(i: Int): Any {
        return clientReportDTOS[i]
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        if (view != null) {
            return view
        }
        val inflate =
            LayoutInflater.from(mActivity).inflate(R.layout.report_item_layout, null as ViewGroup?)
        clientReportDTOS[i].id
        val clientReportHolder = ClientReportHolder(inflate)
        clientReportHolder.clientName.text = clientReportDTOS[i].name
        clientReportHolder.invocieNumber.text = clientReportDTOS[i].invoices.toString()
        val textView = clientReportHolder.paidAmount
        textView.text = SettingsDTO.settingsDTO?.let {
            MyConstants.formatCurrency(
                mActivity,
                it.currencyFormat
            )
        } + MyConstants.formatDecimal(java.lang.Double.valueOf(clientReportDTOS[i].paidAmount))
        inflate.tag = clientReportHolder
        return inflate
    }
}