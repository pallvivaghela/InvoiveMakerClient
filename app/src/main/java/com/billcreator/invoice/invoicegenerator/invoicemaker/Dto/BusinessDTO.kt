package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BusinessDTO : Serializable {
    @SerializedName("bank_information")
    var bankInformation: String? = null

    @SerializedName("check_information")
    var checkInformation: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("fax")
    var fax: String? = null

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("line1")
    var line1: String? = null

    @SerializedName("line2")
    var line2: String? = null

    @SerializedName("line3")
    var line3: String? = null

    @SerializedName("logo")
    var logo: String? = null

    @SerializedName("mobile_no")
    var mobileNo: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("other_payment_information")
    var otherPaymentInformation: String? = null

    @SerializedName("paypal_address")
    var paypalAddress: String? = null

    @SerializedName("phone_no")
    var phoneNo: String? = null

    @SerializedName("reg_no")
    var regNo: String? = null

    @SerializedName("signed_date")
    var signedDate: String? = null

    @SerializedName("signedUrl")
    var signedUrl = ""

    @SerializedName("website")
    var website: String? = null
    override fun toString(): String {
        return "BusinessDTO{id=" + id + ", paypalAddress='" + paypalAddress + '\'' + ", checkInformation='" + checkInformation + '\'' + ", bankInformation='" + bankInformation + '\'' + ", otherPaymentInformation='" + otherPaymentInformation + '\'' + ", name='" + name + '\'' + ", regNo='" + regNo + '\'' + ", line1='" + line1 + '\'' + ", line2='" + line2 + '\'' + ", line3='" + line3 + '\'' + ", phoneNo='" + phoneNo + '\'' + ", mobileNo='" + mobileNo + '\'' + ", fax='" + fax + '\'' + ", email='" + email + '\'' + ", website='" + website + '\'' + ", logo='" + logo + '\'' + '}'
    }
}