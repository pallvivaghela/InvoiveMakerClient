package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

class DiscountDTO {
    var catalogId: Long = 0
    var discountAmount = 0.0
    var discountType = 0
    override fun toString(): String {
        return "DiscountDTO{catalogId=" + catalogId + ", discountType=" + discountType + ", discountAmount=" + discountAmount + '}'
    }
}