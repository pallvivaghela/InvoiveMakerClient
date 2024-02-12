package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.MonthlyReportDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants

class ReportAdapter(
    private val mActivity: Activity,
    private val monthlyReportDTOS: ArrayList<MonthlyReportDTO>
) : BaseAdapter() {
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    class MonthlyReportHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clientsNumber: TextView
        val invocieNumber: TextView
        val monthName: TextView
        val paidAmount: TextView

        init {
            monthName = view.findViewById<View>(R.id.month_name) as TextView
            clientsNumber = view.findViewById<View>(R.id.clients_number) as TextView
            invocieNumber = view.findViewById<View>(R.id.invoice_number) as TextView
            paidAmount = view.findViewById<View>(R.id.paid_amount) as TextView
        }
    }

    override fun getCount(): Int {
        return monthlyReportDTOS.size
    }

    override fun getItem(i: Int): Any {
        return monthlyReportDTOS[i]
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        if (view == null) {
            view = LayoutInflater.from(mActivity)
                .inflate(R.layout.report_item_layout, null as ViewGroup?)
            val monthlyReportHolder = MonthlyReportHolder(view)
            if (monthlyReportDTOS[i].yearOrMonth < 12) {
                monthlyReportHolder.monthName.text =
                    calendarMonths[monthlyReportDTOS[i].yearOrMonth]
                monthlyReportHolder.clientsNumber.text =
                    monthlyReportDTOS[i].totalClients.toString()
            } else {
                monthlyReportHolder.monthName.text = monthlyReportDTOS[i].yearOrMonth.toString()
                monthlyReportHolder.clientsNumber.text =
                    monthlyReportDTOS[i].getTotalClientsPerYear().toString()
            }
            monthlyReportHolder.invocieNumber.text = monthlyReportDTOS[i].totalInvoices.toString()
            val textView = monthlyReportHolder.paidAmount
            textView.text = MyConstants.formatCurrency(
                mActivity,
                SettingsDTO.settingsDTO!!.currencyFormat
            ) + MyConstants.formatDecimal(java.lang.Double.valueOf(monthlyReportDTOS[i].totalPaidAmount))
            view.tag = monthlyReportHolder
        }
        return view
    }

    companion object {
        private val calendarMonths = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
    }
}