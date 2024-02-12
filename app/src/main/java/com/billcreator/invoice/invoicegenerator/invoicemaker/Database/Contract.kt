package com.billcreator.invoice.invoicegenerator.invoicemaker.Database

import android.provider.BaseColumns

class Contract {
    object BusinessInformation : BaseColumns {
        const val ADDRESS_LINE_1 = "ADDRESS_LINE_1"
        const val ADDRESS_LINE_2 = "ADDRESS_LINE_2"
        const val ADDRESS_LINE_3 = "ADDRESS_LINE_3"
        const val BANK_INFORMATION = "BANK_INFORMATION"
        const val CHEQUES_INFORMATION = "CHEQUES_INFORMATION"
        const val EMAIL = "EMAIL"
        const val ESTIMATE_NOTES = "ESTIMATE_NOTES"
        const val FAX = "FAX"
        const val FULLY_PAID = "FULLY_PAID"
        const val INVOICE_NOTES = "INVOICE_NOTES"
        const val LOGO_URL = "LOGO_URL"
        const val MOBILE = "MOBILE"
        const val NAME = "NAME"
        const val OTHER_PAYMENT_INFORMATION = "OTHER_PAYMENT_INFORMATION"
        const val PAYPAL_ADDRESS = "PAYPAL_ADDRESS"
        const val PHONE = "PHONE"
        const val QTY_RATE = "QTY_RATE"
        const val REGISTRATION_NUMBER = "REGISTRATION_NUMBER"
        const val TABLE_NAME = "BUSINESS_INFORMATION"
        const val TAXES = "TAXES"
        const val VAT = "VAT"
        const val WEBSITE = "WEBSITE"
    }

    object Catalog : BaseColumns {
        const val CLIENTS_ID = "CLIENTS_ID"
        const val CREATED_AT = "CREATED_AT"
        const val DISCOUNT = "DISCOUNT"
        const val DISCOUNT_TYPE = "DISCOUNT_TYPE"
        const val DUE_DATE = "DUE_DATE"
        const val ESTIMATE_STATUS = "ESTIMATE_STATUS"
        const val NAME = "NAME"
        const val NOTES = "NOTES"
        const val PAID = "PAID"
        const val PAID_STATUS = "PAID_STATUS"
        const val PO_NUMBER = "PO_NUMBER"
        const val SIGNED_DATE = "SIGNED_DATE"
        const val SIGNED_URL = "SIGNED_URL"
        const val TABLE_NAME = "CATALOG"
        const val TAX_LABEL = "TAX_LABEL"
        const val TAX_RATE = "TAX_RATE"
        const val TAX_TYPE = "TAX_TYPE"
        const val TERMS = "TERMS"
        const val TOTAL_AMOUNT = "TOTAL_AMOUNT"
        const val TYPE = "TYPE"
    }

    object CatalogImages : BaseColumns {
        const val ADDITIONAL_DETAILS = "ADDITIONAL_DETAILS"
        const val CATALOG_ID = "CATALOG_ID"
        const val DESCRIPTION = "DESCRIPTION"
        const val IMAGE_URL = "IMAGE_URL"
        const val TABLE_NAME = "CATALOG_IMAGES"
    }

    object Clients : BaseColumns {
        const val ADDRESS_CONTACT = "ADDRESS_CONTACT"
        const val ADDRESS_LINE_1 = "ADDRESS_LINE_1"
        const val ADDRESS_LINE_2 = "ADDRESS_LINE_2"
        const val ADDRESS_LINE_3 = "ADDRESS_LINE_3"
        const val EMAIL = "EMAIL"
        const val FAX = "FAX"
        const val MOBILE = "MOBILE"
        const val NAME = "NAME"
        const val PHONE = "PHONE"
        const val SHIPPING_ADDRESS_LINE_1 = "SHIPPING_ADDRESS_LINE_1"
        const val SHIPPING_ADDRESS_LINE_2 = "SHIPPING_ADDRESS_LINE_2"
        const val SHIPPING_ADDRESS_LINE_3 = "SHIPPING_ADDRESS_LINE_3"
        const val SHIPPING_ADDRESS_NAME = "SHIPPING_ADDRESS_NAME"
        const val TABLE_NAME = "CLIENTS"
    }

    object Items : BaseColumns {
        const val DESCRIPTION = "DESCRIPTION"
        const val NAME = "NAME"
        const val TABLE_NAME = "ITEMS"
        const val TAXABLE = "TAXABLE"
        const val UNIT_COST = "UNIT_COST"
    }

    object ItemsAssociated : BaseColumns {
        const val CATALOG_ID = "CATALOG_ID"
        const val DESCRIPTION = "DESCRIPTION"
        const val DISCOUNT = "DISCOUNT"
        const val ITEM_NAME = "ITEM_NAME"
        const val QTY = "QTY"
        const val TABLE_NAME = "ITEMS_ASSOCIATED"
        const val TAXABLE = "TAXABLE"
        const val TAX_RATE = "TAX_RATE"
        const val UNIT_COST = "UNIT_COST"
    }

    object Payments : BaseColumns {
        const val AMOUNT = "AMOUNT"
        const val CATALOG_ID = "CATALOG_ID"
        const val DATE = "DATE"
        const val METHOD = "METHOD"
        const val NOTES = "NOTES"
        const val TABLE_NAME = "PAYMENTS"
    }

    object Settings : BaseColumns {
        const val CURRENCY = "CURRENCY"
        const val DATE_FORMAT = "DATE_FORMAT"
        const val TABLE_NAME = "SETTINGS"
    }

    object Shipping : BaseColumns {
        const val AMOUNT = "AMOUNT"
        const val CATALOG_ID = "CATALOG_ID"
        const val FOB = "FOB"
        const val SHIPPING_DATE = "SHIPPING_DATE"
        const val SHIP_VIA = "SHIP_VIA"
        const val TABLE_NAME = "SHIPPING"
        const val TRACKING = "TRACKING"
    }
}