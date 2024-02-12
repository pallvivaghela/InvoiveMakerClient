package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddItemActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.MyItemActivity.Companion.start
import android.app.Activity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemDTO
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.RelativeLayout
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemAssociatedDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import java.util.*
import kotlin.collections.ArrayList

class ItemAdapter(
    private val mActivity: Activity,
    private val itemDTOS: ArrayList<ItemDTO>,
    private val catalogId: Long,
    private val isSearch: Boolean
) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    private val currencySign: String

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val unitCost: TextView
        var viewBackground: RelativeLayout
        @JvmField
        var viewForeground: RelativeLayout

        init {
            itemName = view.findViewById<View>(R.id.item_name) as TextView
            unitCost = view.findViewById<View>(R.id.unit_cost) as TextView
            viewForeground = view.findViewById<View>(R.id.view_foreground) as RelativeLayout
            viewBackground = view.findViewById<View>(R.id.view_background) as RelativeLayout
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.my_item_layout, viewGroup, false)
        )
    }

    override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
        val arrayList = itemDTOS
        if (arrayList != null) {
            val itemDTO = arrayList[i]
            itemHolder.itemName.text = itemDTO.itemName
            val textView = itemHolder.unitCost
            textView.text =
                currencySign + MyConstants.formatDecimal(itemDTO.unitCost)
            itemHolder.itemView.setOnClickListener(View.OnClickListener { view ->

                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ItemAdapter.AnonymousClass1 */
                if (isSearch) {
                    val itemAssociatedDTO = ItemAssociatedDTO()
                    itemAssociatedDTO.itemName = itemDTO.itemName
                    itemAssociatedDTO.unitCost =
                        MyConstants.formatDecimal(itemDTO.unitCost)
                    itemAssociatedDTO.taxAble = itemDTO.texable
                    itemAssociatedDTO.description = itemDTO.itemDescription
                    itemAssociatedDTO.quantity = 1.0
                    start(view.context, itemAssociatedDTO, catalogId, 0, 3)
                    return@OnClickListener
                }
                start(view.context, itemDTO)
            })
        }
    }

    override fun getItemCount(): Int {
        val arrayList = itemDTOS ?: return 0
        return arrayList.size
    }

    fun filter(charSequence: CharSequence, arrayList: ArrayList<ItemDTO>) {
        val arrayList2 = arrayList.clone() as ArrayList<ItemDTO>
        itemDTOS.clear()
        if (charSequence == "") {
            itemDTOS.addAll(arrayList2)
            notifyDataSetChanged()
            return
        }
        val it: Iterator<*> = arrayList2.iterator()
        while (it.hasNext()) {
            val itemDTO = it.next() as ItemDTO
            if (itemDTO.itemName != null && itemDTO.itemName != "" && itemDTO.itemName!!.lowercase(
                    Locale.getDefault()
                ).contains(charSequence.toString().lowercase(Locale.getDefault()))
            ) {
                itemDTOS.add(itemDTO)
            }
        }
        notifyDataSetChanged()
    }

    fun removeItem(i: Int) {
        itemDTOS.removeAt(i)
        notifyItemRemoved(i)
    }

    fun restoreItem(itemDTO: ItemDTO, i: Int) {
        itemDTOS.add(i, itemDTO)
        notifyItemInserted(i)
    }

    init {
        currencySign =
            SettingsDTO.settingsDTO?.let { MyConstants.formatCurrency(mActivity, it.currencyFormat) }
                .toString()
    }
}