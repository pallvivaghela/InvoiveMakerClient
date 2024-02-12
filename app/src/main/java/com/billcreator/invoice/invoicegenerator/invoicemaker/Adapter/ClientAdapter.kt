package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddClientActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import java.util.*

class ClientAdapter(
    private val mActivity: Activity,
    private val clientDTOS: ArrayList<ClientDTO>,
    private val catalogId: Long,
    z: Boolean
) : RecyclerView.Adapter<ClientAdapter.ItemHolder>() {
    private val currencySign: String
    private var fromCatalog = false
    private var isInvoice = false

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clientName: TextView
        val totalAmount: TextView
        val totalAmountEstimate: TextView

        init {
            clientName = view.findViewById<View>(R.id.client_name) as TextView
            totalAmount = view.findViewById<View>(R.id.total_amount) as TextView
            totalAmountEstimate = view.findViewById<View>(R.id.total_amount_estimate) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.client_item_layout, viewGroup, false)
        )
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
        val arrayList = clientDTOS
        if (arrayList != null) {
            val clientDTO = arrayList[i]
            itemHolder.clientName.text = clientDTO.clientName
            if (fromCatalog) {
                itemHolder.totalAmount.visibility = 4
                itemHolder.totalAmountEstimate.visibility = 4
            } else {
                if (clientDTO.totalInvoice > 1) {
                    val textView = itemHolder.totalAmount
                    textView.text =
                        currencySign + clientDTO.totalAmount + ", " + clientDTO.totalInvoice + " Invoices"
                } else {
                    val textView2 = itemHolder.totalAmount
                    textView2.text =
                        currencySign + clientDTO.totalAmount + ", " + clientDTO.totalInvoice + " Invoice"
                }
                if (clientDTO.totalEstimate > 1) {
                    val textView3 = itemHolder.totalAmountEstimate
                    textView3.text =
                        currencySign + clientDTO.totalAmountEstimate + ", " + clientDTO.totalEstimate + " Estimates"
                } else {
                    val textView4 = itemHolder.totalAmountEstimate
                    textView4.text =
                        currencySign + clientDTO.totalAmountEstimate + ", " + clientDTO.totalEstimate + " Estimate"
                }
            }
            itemHolder.itemView.setOnClickListener(View.OnClickListener { view ->

                if (catalogId > 0) {
                    clientDTO.catalogId = catalogId
                    isInvoice = true
                } else {
                    isInvoice = false
                }
                if (fromCatalog) {
                    if (catalogId == 0L) {
                        clientDTO.catalogId = MyConstants.createNewInvoice()
                    }
                    LoadDatabase.instance!!.addInvoiceClient(clientDTO)
                    mActivity.finish()
                    return@OnClickListener
                }
                val context = view.context
                start(
                    context,
                    clientDTO,
                    clientDTO.catalogId,
                    isInvoice
                )
            })
        }
    }

    override fun getItemCount(): Int {
        val arrayList = clientDTOS ?: return 0
        return arrayList.size
    }

    fun filter(str: String, arrayList: ArrayList<ClientDTO>) {
        val arrayList2 = arrayList.clone() as ArrayList<ClientDTO>
        clientDTOS.clear()
        if (str == "") {
            clientDTOS.addAll(arrayList2)
            notifyDataSetChanged()
            return
        }
        val it: Iterator<*> = arrayList2.iterator()
        while (it.hasNext()) {
            val clientDTO = it.next() as ClientDTO?
            if (clientDTO != null && clientDTO.clientName != null && clientDTO.clientName != "" && clientDTO.clientName!!.lowercase(
                    Locale.getDefault()
                ).contains(str.lowercase(Locale.getDefault()))
            ) {
                clientDTOS.add(clientDTO)
            }
        }
        notifyDataSetChanged()
    }

    fun removeItem(i: Int) {
        clientDTOS.removeAt(i)
        notifyItemRemoved(i)
    }

    fun restoreItem(clientDTO: ClientDTO, i: Int) {
        clientDTOS.add(i, clientDTO)
        notifyItemInserted(i)
    }

    init {
        currencySign =
            MyConstants.formatCurrency(mActivity, SettingsDTO.settingsDTO!!.currencyFormat)
        fromCatalog = z
    }
}