package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import java.util.ArrayList

class HistoryAdapter(private val mActivity: Activity, private val itemDTOS: ArrayList<String>?) :
    RecyclerView.Adapter<HistoryAdapter.ItemHolder>() {
    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val creationDate: TextView

        init {
            creationDate = view.findViewById<View>(R.id.creation_date) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.invoice_history_layout, viewGroup, false)
        )
    }

    override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
        if (itemDTOS != null) {
            itemHolder.creationDate.text = itemDTOS[i]
        }
    }

    override fun getItemCount(): Int {
        val arrayList = itemDTOS ?: return 0
        return arrayList.size
    }
}