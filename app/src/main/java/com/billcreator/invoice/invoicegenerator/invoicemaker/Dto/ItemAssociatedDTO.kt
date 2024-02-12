package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import com.google.gson.annotations.SerializedName
import com.itextpdf.text.xml.xmp.DublinCoreProperties
import java.io.Serializable

class ItemAssociatedDTO : Serializable {
    @SerializedName("catalog_id")
    var catalogId: Long = 0

    @SerializedName(DublinCoreProperties.DESCRIPTION)
    var description: String? = null

    @SerializedName("discount")
    var discount = 0.0

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("item_name")
    var itemName: String? = null

    @SerializedName("quantity")
    var quantity = 1.0

    @SerializedName("tax_able")
    var taxAble = 0

    @SerializedName("tax_rate")
    var taxRate = 0.0
    var totalAmount = 0.0

    @SerializedName("unit_cost")
    var unitCost = 0.0
    override fun toString(): String {
        return "ItemAssociatedDTO{id=" + id + ", itemName='" + itemName + '\'' + ", description='" + description + '\'' + ", unitCost=" + unitCost + ", taxAble=" + taxAble + ", catalogId=" + catalogId + ", quantity=" + quantity + ", taxRate=" + taxRate + ", discount=" + discount + ", totalAmount=" + totalAmount + '}'
    }
}