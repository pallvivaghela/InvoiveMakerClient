package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import com.google.gson.annotations.SerializedName
import com.itextpdf.text.xml.xmp.DublinCoreProperties
import java.io.Serializable

class ImageDTO : Serializable {
    @SerializedName("additional_details")
    var additionalDetails: String? = null

    @SerializedName("catalog_id")
    var catalogId: Long = 0

    @SerializedName(DublinCoreProperties.DESCRIPTION)
    var description: String? = null

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("img_url")
    var imageUrl: String? = null
    override fun toString(): String {
        return "ImageDTO{id=" + id + ", catalogId=" + catalogId + ", imageUrl='" + imageUrl + '\'' + ", description='" + description + '\'' + ", additionalDetails='" + additionalDetails + '\'' + '}'
    }
}