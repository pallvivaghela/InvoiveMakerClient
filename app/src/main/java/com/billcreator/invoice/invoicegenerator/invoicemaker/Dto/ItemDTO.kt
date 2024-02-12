package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import java.io.Serializable

class ItemDTO : Serializable {
    var id: Long = 0
    var itemDescription: String? = null
    var itemName: String? = null
    var texable = 0
    var unitCost = 0.0

    constructor() {}
    constructor(str: String?, str2: String?, d: Double, i: Int) {
        itemName = str
        itemDescription = str2
        unitCost = d
        texable = i
    }

    override fun toString(): String {
        return "ItemDTO{id=" + id + ", itemName='" + itemName + '\'' + ", itemDescription='" + itemDescription + '\'' + ", unitCost=" + unitCost + ", texable=" + texable + '}'
    }
}