package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

class TaxDTO {
    var catalogId: Long = 0
    var taxLabel: String? = null
    var taxRate = 0.0
    var taxType = 0
    override fun toString(): String {
        return "TaxDTO{catalogId=" + catalogId + ", taxType=" + taxType + ", taxRate=" + taxRate + ", taxLabel='" + taxLabel + '\'' + '}'
    }
}