package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

class SignedDTO {
    var catalogId: Long = 0
    var signedDate = ""
    var signedUrl = ""
    override fun toString(): String {
        return "SignedDTO{catalogId=" + catalogId + ", signedDate='" + signedDate + '\'' + ", signedUrl='" + signedUrl + '\'' + '}'
    }
}