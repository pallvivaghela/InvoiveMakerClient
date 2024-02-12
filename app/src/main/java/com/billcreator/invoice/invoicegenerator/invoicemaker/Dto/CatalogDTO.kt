package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import com.google.gson.annotations.SerializedName
import com.itextpdf.text.xml.xmp.DublinCoreProperties
import java.io.Serializable

class CatalogDTO : Serializable, Cloneable {
    @SerializedName("catalog_name")
    var catalogName: String? = null

    @SerializedName("client_dto")
    var clientDTO = ClientDTO()

    @SerializedName("creation_date")
    var creationDate: String? = null

    @SerializedName("discount")
    var discount = 0.0

    @SerializedName("discount_amount")
    var discountAmount = 0.0

    @SerializedName("discount_type")
    var discountType = 0

    @SerializedName("due_date")
    var dueDate: String? = null

    @SerializedName("estimate_status")
    var estimateStatus = 0
    var id: Long = 0

    @SerializedName("invoice_shipping_dto")
    var invoiceShippingDTO = InvoiceShippingDTO()

    @SerializedName("notes")
    var notes: String? = null

    @SerializedName("paid_amount")
    var paidAmount = 0

    @SerializedName("paid_status")
    var paidStatus = 0

    @SerializedName("po_number")
    var poNumber: String? = null

    @SerializedName("signed_date")
    var signedDate: String? = null
    var signedUrl = ""

    @SerializedName("subtotal_amount")
    var subTotalAmount = 0.0

    @SerializedName("tax_amount")
    var taxAmount = 0.0

    @SerializedName("tax_label")
    var taxLabel: String? = null

    @SerializedName("tax_rate")
    var taxRate = 0.0

    @SerializedName("tax_type")
    var taxType = 3

    @SerializedName("terms")
    var terms = 0

    @SerializedName("total_amount")
    var totalAmount = 0

    @SerializedName(DublinCoreProperties.TYPE)
    var type = 0

    @Throws(CloneNotSupportedException::class)  // java.lang.Object
    public override fun clone(): CatalogDTO {
        return super.clone() as CatalogDTO
    }
}