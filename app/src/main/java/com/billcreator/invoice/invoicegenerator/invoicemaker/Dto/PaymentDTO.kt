package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

class PaymentDTO {
    var catalogId: Long = 0
    var id: Long = 0
    var paidAmount = 0.0
    var paymentDate: String? = null
    var paymentMethod: String? = null
    var paymentNotes: String? = null
    override fun toString(): String {
        return "PaymentDTO{catalogId=" + catalogId + ", paidAmount=" + paidAmount + ", paymentDate='" + paymentDate + '\'' + ", paymentMethod='" + paymentMethod + '\'' + '}'
    }
}