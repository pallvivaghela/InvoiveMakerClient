package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import java.io.Serializable

class ClientDTO : Serializable {
    var addressLine1: String? = null
    var addressLine2: String? = null
    var addressLine3: String? = null
    var catalogId: Long = 0
    var clientName: String? = null
    var contactAdress: String? = null
    var dueAmount = 0.0
    var emailAddress: String? = null
    var faxNo: String? = null
    var id: Long = 0
    var invoiceDate: String? = null
    var mobileNo: String? = null
    var phoneNo: String? = null
    var shippingAddress: String? = null
    var shippingLine1: String? = null
    var shippingLine2: String? = null
    var shippingLine3: String? = null
    var totalAmount = 0.0
    var totalAmountEstimate = 0.0
    var totalEstimate = 0
    var totalInvoice = 0
    override fun toString(): String {
        return "ClientDTO{id=" + id + ", catalogId=" + catalogId + ", totalAmount=" + totalAmount + ", dueAmount=" + dueAmount + ", invoiceDate='" + invoiceDate + '\'' + ", totalInvoice=" + totalInvoice + ", clientName='" + clientName + '\'' + ", emailAddress='" + emailAddress + '\'' + ", mobileNo='" + mobileNo + '\'' + ", phoneNo='" + phoneNo + '\'' + ", faxNo='" + faxNo + '\'' + ", contactAdress='" + contactAdress + '\'' + ", addressLine1='" + addressLine1 + '\'' + ", addressLine2='" + addressLine2 + '\'' + ", addressLine3='" + addressLine3 + '\'' + ", shippingAddress='" + shippingAddress + '\'' + ", shippingLine1='" + shippingLine1 + '\'' + ", shippingLine2='" + shippingLine2 + '\'' + ", shippingLine3='" + shippingLine3 + '\'' + '}'
    }
}