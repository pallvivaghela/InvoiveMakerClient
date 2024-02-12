package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import java.io.Serializable

class InvoiceShippingDTO : Serializable {
    var amount = 0.0
    var catalogId: Long = 0
    var fob: String? = null
    var id: Long = 0
    var shipVia: String? = null
    var shippingDate: String? = null
    var tracking: String? = null
    override fun toString(): String {
        return "InvoiceShippingDTO{id=" + id + ", catalogId=" + catalogId + ", amount=" + amount + ", shippingDate='" + shippingDate + '\'' + ", shipVia='" + shipVia + '\'' + ", tracking='" + tracking + '\'' + ", fob='" + fob + '\'' + '}'
    }
}