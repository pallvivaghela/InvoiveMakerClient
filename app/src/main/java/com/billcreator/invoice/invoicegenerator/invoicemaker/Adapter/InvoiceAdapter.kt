package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceDetailsActivity.Companion.start
import android.app.Activity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.CatalogDTO
import androidx.recyclerview.widget.RecyclerView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.InvoiceAdapter.MainInvoiceHolder
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import java.util.*
import kotlin.collections.ArrayList

class InvoiceAdapter(
    private val mActivity: Activity,
    private val invoiceDTOS: ArrayList<CatalogDTO>
) : RecyclerView.Adapter<MainInvoiceHolder>() {
    private val currency_sign: String

    class MainInvoiceHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clientName: TextView
        val dueDate: TextView
        val invoiceAmount: TextView
        val invoiceName: TextView

        init {
            clientName = view.findViewById<View>(R.id.client_name) as TextView
            invoiceAmount = view.findViewById<View>(R.id.invoice_amount) as TextView
            invoiceName = view.findViewById<View>(R.id.invoice_name) as TextView
            dueDate = view.findViewById<View>(R.id.due_date) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MainInvoiceHolder {
        return MainInvoiceHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.main_invoice_layout, viewGroup, false)
        )
    }

    override fun onBindViewHolder(mainInvoiceHolder: MainInvoiceHolder, i: Int) {
        val arrayList = invoiceDTOS
        if (arrayList != null) {
            val catalogDTO = arrayList[i]
            mainInvoiceHolder.invoiceName.text = catalogDTO.catalogName
            if (catalogDTO.paidStatus == 2) {
                val textView = mainInvoiceHolder.invoiceAmount
                textView.text =
                    currency_sign + MyConstants.formatDecimal(catalogDTO.totalAmount.toDouble())
            } else {
                val textView2 = mainInvoiceHolder.invoiceAmount
                textView2.text =
                    currency_sign + MyConstants.formatDecimal(catalogDTO.totalAmount - catalogDTO.paidAmount.toDouble())
            }
            if (catalogDTO.clientDTO.id == 0L) {
                mainInvoiceHolder.clientName.text = "No client"
            } else {
                mainInvoiceHolder.clientName.text = catalogDTO.clientDTO.clientName
            }
            if (catalogDTO.paidStatus == 2) {
                val textView3 = mainInvoiceHolder.dueDate
                textView3.text = "Paid " + MyConstants.formatDate(
                    mActivity,
                    catalogDTO.creationDate!!.toLong(),
                    SettingsDTO.settingsDTO!!.dateFormat
                )
            } else if (catalogDTO.dueDate != null && catalogDTO!!.dueDate!!.length > 0) {
                val i2 =
                    if ((catalogDTO.dueDate!!.toLong() - Calendar.getInstance().timeInMillis) / 86400000 > 0) 1 else if ((catalogDTO.dueDate!!.toLong() - Calendar.getInstance().timeInMillis) / 86400000 == 0L) 0 else -1
                if (i2 >= 0) {
                    val textView4 = mainInvoiceHolder.dueDate
                    textView4.text = "Due " + MyConstants.formatDate(
                        mActivity,
                        catalogDTO.dueDate!!.toLong(),
                        SettingsDTO.settingsDTO!!.dateFormat
                    )
                    mainInvoiceHolder.dueDate.setTextColor(mActivity.resources.getColor(R.color.black))
                } else if (i2 < 0) {
                    val textView5 = mainInvoiceHolder.dueDate
                    textView5.text = "Due " + MyConstants.formatDate(
                        mActivity,
                        catalogDTO.dueDate!!.toLong(),
                        SettingsDTO.settingsDTO!!.dateFormat
                    )
                    mainInvoiceHolder.dueDate.setTextColor(mActivity.resources.getColor(R.color.delete_color_bg))
                }
            } else if (catalogDTO.terms == 1) {
                mainInvoiceHolder.dueDate.text = "Due on receipt"
                mainInvoiceHolder.dueDate.setTextColor(mActivity.resources.getColor(R.color.black))
            } else if (catalogDTO.terms == 0) {
                mainInvoiceHolder.dueDate.text = ""
            }
            mainInvoiceHolder.itemView.setOnClickListener { view ->
                start(view.context, catalogDTO)
            }
        }
    }

    override fun getItemCount(): Int {
        return invoiceDTOS.size
    }

    fun removeItem(i: Int) {
        invoiceDTOS.removeAt(i)
        notifyItemRemoved(i)
    }

    fun restoreItem(catalogDTO: CatalogDTO, i: Int) {
        invoiceDTOS.add(i, catalogDTO)
        notifyItemInserted(i)
    }

    fun filter(charSequence: CharSequence, arrayList: ArrayList<CatalogDTO>): Boolean {
        val arrayList2 = arrayList.clone() as ArrayList<CatalogDTO>
        invoiceDTOS.clear()
        if (charSequence == "") {
            invoiceDTOS.addAll(arrayList2)
            notifyDataSetChanged()
            return false
        }
        val it: Iterator<*> = arrayList2.iterator()
        while (it.hasNext()) {
            val catalogDTO = it.next() as CatalogDTO
            if (catalogDTO.clientDTO != null && catalogDTO.clientDTO.clientName != null && catalogDTO.clientDTO.clientName != "" && catalogDTO.clientDTO.clientName!!.lowercase(
                    Locale.getDefault()
                ).contains(charSequence.toString().lowercase(Locale.getDefault()))
            ) {
                invoiceDTOS.add(catalogDTO)
            }
        }
        notifyDataSetChanged()
        return if (invoiceDTOS.size != 0) {
            true
        } else false
    }

    init {
        currency_sign =
            MyConstants.formatCurrency(mActivity, SettingsDTO.settingsDTO!!.currencyFormat)
    }
}