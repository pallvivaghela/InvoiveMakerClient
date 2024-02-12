package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddItemActivity.Companion.start
import android.app.Activity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemAssociatedDTO
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import java.util.ArrayList

class ItemAssociatedAdapter(
    private val mActivity: Activity,
    private val dtos: ArrayList<ItemAssociatedDTO>,
    private var discountType: Int,
    private var taxType: Int
) : RecyclerView.Adapter<ItemAssociatedAdapter.ItemHolder>() {
    private val currencySign: String

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val totalCost: TextView
        val unitCostQuantity: TextView

        init {
            itemName = view.findViewById<View>(R.id.item_name) as TextView
            unitCostQuantity = view.findViewById<View>(R.id.unit_cost_quantity) as TextView
            totalCost = view.findViewById<View>(R.id.total_cost) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.invoice_item_layout, viewGroup, false)
        )
    }

    override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
        val arrayList: ArrayList<ItemAssociatedDTO> = dtos
        if (arrayList != null) {
            val itemAssociatedDTO = arrayList[i]
            itemHolder.itemName.text = itemAssociatedDTO.itemName
            val textView = itemHolder.unitCostQuantity
            textView.text =
                itemAssociatedDTO.quantity.toString() + " x " + currencySign + itemAssociatedDTO.unitCost
            var unitCost = itemAssociatedDTO.unitCost * itemAssociatedDTO.quantity
            if (discountType == 1) {
                unitCost -= (itemAssociatedDTO.discount * unitCost) * 0.01
            }
            val textView2 = itemHolder.totalCost
            textView2.text =
                currencySign + MyConstants.formatDecimal(java.lang.Double.valueOf(unitCost))
            itemHolder.itemView.setOnClickListener(
                View.OnClickListener
                {
                    val activity = mActivity
                    val itemAssociatedDTO2 = itemAssociatedDTO
                    start(
                        activity,
                        itemAssociatedDTO2,
                        itemAssociatedDTO2.catalogId,
                        discountType,
                        taxType
                    )
                })
        }
    }

    override fun getItemCount(): Int {
        val arrayList: ArrayList<ItemAssociatedDTO> = dtos ?: return 0
        return arrayList.size
    }

    fun removeItem(i: Int) {
        dtos.removeAt(i)
        notifyItemRemoved(i)
    }

    fun restoreItem(itemAssociatedDTO: ItemAssociatedDTO, i: Int) {
        dtos.add(i, itemAssociatedDTO)
        notifyItemInserted(i)
    }

    fun updateDiscount(i: Int) {
        discountType = i
    }

    fun updateTax(i: Int) {
        taxType = i
    }

    init {
        currencySign =
            MyConstants.formatCurrency(mActivity, SettingsDTO.settingsDTO!!.currencyFormat)
    }
}