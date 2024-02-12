package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddPhotoActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ImageDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R

class ItemImageAdapter(
    private val mActivity: Activity,
    private val imageDTOS: ArrayList<ImageDTO>
) : RecyclerView.Adapter<ItemImageAdapter.ItemHolder>() {
    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageUrl: TextView

        init {
            imageUrl = view.findViewById<View>(R.id.image_url) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_image_layout, viewGroup, false)
        )
    }

    override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
        val arrayList: ArrayList<ImageDTO> = imageDTOS
        if (arrayList != null) {
            val imageDTO = arrayList[i]
            itemHolder.imageUrl.text = imageDTO.description
            itemHolder.itemView.setOnClickListener(View.OnClickListener { view ->

                val context = view.context
                val imageDTO2 = imageDTO
                start(context, imageDTO2, imageDTO2.catalogId)
            })
        }
    }

    override fun getItemCount(): Int {
        val arrayList: ArrayList<ImageDTO> = imageDTOS ?: return 0
        return arrayList.size
    }
}