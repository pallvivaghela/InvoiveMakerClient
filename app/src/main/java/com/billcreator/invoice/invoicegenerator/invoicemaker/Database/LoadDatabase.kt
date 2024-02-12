package com.billcreator.invoice.invoicegenerator.invoicemaker.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseManager.Companion.getInstance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.*
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp
import kotlin.math.roundToInt

class LoadDatabase private constructor() : DatabaseOpenClose() {
    private var myDb: SQLiteDatabase? = null
    fun viewData() {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_ALL_FROM_CLIENTS, null)
        rawQuery.moveToFirst()
        for (i in 0 until rawQuery.count) {
            for (i2 in 1 until rawQuery.columnCount) {
            }
            rawQuery.moveToNext()
        }
        closeDB()
    }

    val settings: SettingsDTO
        get() {
            openDb()
            val rawQuery = myDb!!.rawQuery(Query.SELECT_ALL_FROM_SETTINGS, null)
            val settingsDTO = SettingsDTO()
            while (rawQuery.moveToNext()) {
                settingsDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
                settingsDTO.currencyFormat =
                    rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Settings.CURRENCY))
                settingsDTO.dateFormat =
                    rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Settings.DATE_FORMAT))
            }
            rawQuery.close()
            closeDB()
            return settingsDTO
        }

    fun updateSettings(settingsDTO: SettingsDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Settings.CURRENCY, Integer.valueOf(settingsDTO.currencyFormat))
        contentValues.put(Contract.Settings.DATE_FORMAT, Integer.valueOf(settingsDTO.dateFormat))
        val update = myDb!!.update(
            Contract.Settings.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + settingsDTO.id)
        )
        closeDB()
        SettingsDTO.settingsDTO = settingsDTO
        try {
            DataProcessor.instance?.notifyListeners(Gson().toJson(settingsDTO), 321)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    val businessInformation: BusinessDTO
        get() {
            openDb()
            val rawQuery = myDb!!.rawQuery(Query.SELECT_ALL_FROM_BUSINESS_INFORMATION, null)
            val businessDTO = BusinessDTO()
            while (rawQuery.moveToNext()) {
                businessDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
                businessDTO.name = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
                businessDTO.regNo =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.REGISTRATION_NUMBER))
                businessDTO.line1 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_1"))
                businessDTO.line2 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_2"))
                businessDTO.line3 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_3"))
                businessDTO.phoneNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("PHONE"))
                businessDTO.mobileNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("MOBILE"))
                businessDTO.fax = rawQuery.getString(rawQuery.getColumnIndexOrThrow("FAX"))
                businessDTO.email = rawQuery.getString(rawQuery.getColumnIndexOrThrow("EMAIL"))
                businessDTO.website =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.WEBSITE))
                businessDTO.paypalAddress =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.PAYPAL_ADDRESS))
                businessDTO.checkInformation =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.CHEQUES_INFORMATION))
                businessDTO.bankInformation =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.BANK_INFORMATION))
                businessDTO.otherPaymentInformation =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.OTHER_PAYMENT_INFORMATION))
                businessDTO.logo =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.BusinessInformation.LOGO_URL))
            }
            rawQuery.close()
            closeDB()
            return businessDTO
        }

    fun saveBusinessInformation(businessDTO: BusinessDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", businessDTO.name)
        contentValues.put(Contract.BusinessInformation.REGISTRATION_NUMBER, businessDTO.regNo)
        contentValues.put("ADDRESS_LINE_1", businessDTO.line1)
        contentValues.put("ADDRESS_LINE_2", businessDTO.line2)
        contentValues.put("ADDRESS_LINE_3", businessDTO.line3)
        contentValues.put("PHONE", businessDTO.phoneNo)
        contentValues.put("MOBILE", businessDTO.mobileNo)
        contentValues.put("FAX", businessDTO.fax)
        contentValues.put("EMAIL", businessDTO.email)
        contentValues.put(Contract.BusinessInformation.WEBSITE, businessDTO.website)
        contentValues.put(Contract.BusinessInformation.PAYPAL_ADDRESS, businessDTO.paypalAddress)
        contentValues.put(
            Contract.BusinessInformation.CHEQUES_INFORMATION,
            businessDTO.checkInformation
        )
        contentValues.put(
            Contract.BusinessInformation.BANK_INFORMATION,
            businessDTO.bankInformation
        )
        contentValues.put(
            Contract.BusinessInformation.OTHER_PAYMENT_INFORMATION,
            businessDTO.otherPaymentInformation
        )
        contentValues.put(Contract.BusinessInformation.LOGO_URL, businessDTO.logo)
        val insert = myDb!!.insert(Contract.BusinessInformation.TABLE_NAME, null, contentValues)
        businessDTO.id = insert
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(businessDTO),
                MyConstants.ACTION_BUSINESS_INFORMATION_ADDED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insert
    }

    fun updateBusinessInformation(businessDTO: BusinessDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", businessDTO.name)
        contentValues.put(Contract.BusinessInformation.REGISTRATION_NUMBER, businessDTO.regNo)
        contentValues.put("ADDRESS_LINE_1", businessDTO.line1)
        contentValues.put("ADDRESS_LINE_2", businessDTO.line2)
        contentValues.put("ADDRESS_LINE_3", businessDTO.line3)
        contentValues.put("PHONE", businessDTO.phoneNo)
        contentValues.put("MOBILE", businessDTO.mobileNo)
        contentValues.put("FAX", businessDTO.fax)
        contentValues.put("EMAIL", businessDTO.email)
        contentValues.put(Contract.BusinessInformation.WEBSITE, businessDTO.website)
        contentValues.put(Contract.BusinessInformation.PAYPAL_ADDRESS, businessDTO.paypalAddress)
        contentValues.put(
            Contract.BusinessInformation.CHEQUES_INFORMATION,
            businessDTO.checkInformation
        )
        contentValues.put(
            Contract.BusinessInformation.BANK_INFORMATION,
            businessDTO.bankInformation
        )
        contentValues.put(
            Contract.BusinessInformation.OTHER_PAYMENT_INFORMATION,
            businessDTO.otherPaymentInformation
        )
        val update = myDb!!.update(
            Contract.BusinessInformation.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + businessDTO.id)
        )
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(businessDTO),
                MyConstants.ACTION_BUSINESS_INFORMATION_UPDATED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun updateBusinessLogo(j: Long, str: String?): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.BusinessInformation.LOGO_URL, str)
        val update = myDb!!.update(
            Contract.BusinessInformation.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + j)
        )
        closeDB()
        return update
    }

    fun saveMyItem(itemDTO: ItemDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", itemDTO.itemName)
        contentValues.put("UNIT_COST", java.lang.Double.valueOf(itemDTO.unitCost))
        contentValues.put("DESCRIPTION", itemDTO.itemDescription)
        contentValues.put("TAXABLE", Integer.valueOf(itemDTO.texable))
        val insert = myDb!!.insert(Contract.Items.TABLE_NAME, null, contentValues)
        closeDB()
        return insert
    }

    val myItems: ArrayList<ItemDTO>
        get() {
            openDb()
            val rawQuery = myDb!!.rawQuery(Query.SELECT_ALL_FROM_ITEMS, null)
            val arrayList = ArrayList<ItemDTO>()
            while (rawQuery.moveToNext()) {
                val itemDTO = ItemDTO()
                itemDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
                itemDTO.itemName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
                itemDTO.unitCost =
                    rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("UNIT_COST")).toDouble()
                itemDTO.itemDescription =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("DESCRIPTION"))
                itemDTO.texable = rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TAXABLE"))
                arrayList.add(itemDTO)
            }
            rawQuery.close()
            closeDB()
            return arrayList
        }

    fun updateMyItem(itemDTO: ItemDTO) {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", itemDTO.itemName)
        contentValues.put("UNIT_COST", java.lang.Double.valueOf(itemDTO.unitCost))
        contentValues.put("DESCRIPTION", itemDTO.itemDescription)
        contentValues.put("TAXABLE", Integer.valueOf(itemDTO.texable))
        myDb!!.update(Contract.Items.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + itemDTO.id))
        closeDB()
    }

    fun deleteMyItem(j: Long) {
        openDb()
        myDb!!.delete(Contract.Items.TABLE_NAME, "_id = ?", arrayOf("" + j))
        closeDB()
    }

    fun saveInvoice(catalogDTO: CatalogDTO): Long {
        catalogDTO.type = MyConstants.CATALOG_TYPE
        openDb()
        val contentValues = ContentValues()
        contentValues.put(
            Contract.Catalog.CLIENTS_ID,
            java.lang.Long.valueOf(catalogDTO.clientDTO.id)
        )
        contentValues.put(Contract.Catalog.TYPE, Integer.valueOf(catalogDTO.type))
        contentValues.put(Contract.Catalog.DISCOUNT_TYPE, Integer.valueOf(catalogDTO.discountType))
        contentValues.put("DISCOUNT", java.lang.Double.valueOf(catalogDTO.discount))
        contentValues.put(Contract.Catalog.TAX_TYPE, Integer.valueOf(catalogDTO.taxType))
        contentValues.put(Contract.Catalog.TAX_LABEL, catalogDTO.taxLabel)
        contentValues.put("TAX_RATE", java.lang.Double.valueOf(catalogDTO.taxRate))
        contentValues.put("NAME", catalogDTO.catalogName)
        contentValues.put(Contract.Catalog.CREATED_AT, catalogDTO.creationDate)
        contentValues.put(Contract.Catalog.TERMS, Integer.valueOf(catalogDTO.terms))
        contentValues.put(Contract.Catalog.DUE_DATE, catalogDTO.dueDate)
        contentValues.put(Contract.Catalog.PO_NUMBER, catalogDTO.poNumber)
        contentValues.put(Contract.Catalog.PAID, java.lang.String.valueOf(catalogDTO.paidAmount))
        contentValues.put(
            Contract.Catalog.TOTAL_AMOUNT,
            java.lang.String.valueOf(catalogDTO.totalAmount)
        )
        contentValues.put(Contract.Catalog.SIGNED_DATE, catalogDTO.signedDate)
        contentValues.put("NOTES", catalogDTO.notes)
        contentValues.put(Contract.Catalog.PAID_STATUS, Integer.valueOf(catalogDTO.paidStatus))
        contentValues.put(
            Contract.Catalog.ESTIMATE_STATUS,
            Integer.valueOf(catalogDTO.estimateStatus)
        )
        contentValues.put(Contract.Catalog.TYPE, Integer.valueOf(catalogDTO.type))
        val insert = myDb!!.insert(Contract.Catalog.TABLE_NAME, null, contentValues)
        catalogDTO.id = insert
        catalogDTO.clientDTO = getSingleClient(catalogDTO.clientDTO.id)
        try {
            DataProcessor.instance?.notifyListeners(Gson().toJson(catalogDTO), 2001)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insert
    }

    fun getSingleCatalog(j: Long): CatalogDTO {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_SINGLE_CATALOG, arrayOf("" + j))
        val catalogDTO = CatalogDTO()
        while (rawQuery.moveToNext()) {
            catalogDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            catalogDTO.type =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TYPE))
            catalogDTO.discountType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DISCOUNT_TYPE))
            catalogDTO.discount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")).toDouble()
            catalogDTO.taxType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_TYPE))
            catalogDTO.taxLabel =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_LABEL))
            catalogDTO.taxRate =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")).toDouble()
            catalogDTO.catalogName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
            catalogDTO.creationDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.CREATED_AT))
            catalogDTO.terms =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TERMS))
            catalogDTO.dueDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DUE_DATE))
            catalogDTO.poNumber =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PO_NUMBER))
            catalogDTO.paidAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID))
                    .roundToInt()
            catalogDTO.paidStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID_STATUS))
            catalogDTO.estimateStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.ESTIMATE_STATUS))
            catalogDTO.totalAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .roundToInt()
            catalogDTO.signedDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_DATE))
//            catalogDTO.signedUrl =
//                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_URL))
            catalogDTO.notes = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES"))
            val singleClient = getSingleClient(
                rawQuery.getInt(
                    rawQuery.getColumnIndexOrThrow(
                        Contract.Catalog.CLIENTS_ID
                    )
                ).toLong()
            )
            if (singleClient.id > 0) {
                catalogDTO.clientDTO = singleClient
            }
            catalogDTO.invoiceShippingDTO = getInvoiceShipping(catalogDTO.id)
        }
        rawQuery.close()
        closeDB()
        return catalogDTO
    }

    val allInvoices: ArrayList<CatalogDTO>
        get() = getFromCatalogs(Query.SELECT_INVOICES_FROM_CATALOG)
    val allEstimates: ArrayList<CatalogDTO>
        get() = getFromCatalogs(Query.SELECT_ESTIMATES_FROM_CATALOG)

    fun getFromCatalogs(str: String?): ArrayList<CatalogDTO> {
        openDb()
        val rawQuery = myDb!!.rawQuery(str, null)
        val arrayList = ArrayList<CatalogDTO>()
        while (rawQuery.moveToNext()) {
            val catalogDTO = CatalogDTO()
            catalogDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            catalogDTO.type =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TYPE))
            catalogDTO.catalogName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
            catalogDTO.creationDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.CREATED_AT))
            catalogDTO.terms =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TERMS))
            catalogDTO.dueDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DUE_DATE))
            catalogDTO.poNumber =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PO_NUMBER))
            catalogDTO.discountType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DISCOUNT_TYPE))
            catalogDTO.discount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")).toDouble()
            catalogDTO.taxType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_TYPE))
            catalogDTO.taxLabel =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_LABEL))
            catalogDTO.taxRate =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")).toDouble()
            catalogDTO.paidAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID))
                    .roundToInt()
            catalogDTO.paidStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID_STATUS))
            catalogDTO.estimateStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.ESTIMATE_STATUS))
            catalogDTO.totalAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .roundToInt()
            catalogDTO.signedDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_DATE))
//            catalogDTO.signedUrl = rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_URL))
            catalogDTO.notes = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES"))
            val singleClient = getSingleClient(
                rawQuery.getInt(
                    rawQuery.getColumnIndexOrThrow(
                        Contract.Catalog.CLIENTS_ID
                    )
                ).toLong()
            )
            if (singleClient.id > 0) {
                catalogDTO.clientDTO = singleClient
            }
            catalogDTO.invoiceShippingDTO = getInvoiceShipping(catalogDTO.id)
            arrayList.add(catalogDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    fun updateInvoice(catalogDTO: CatalogDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.TYPE, Integer.valueOf(catalogDTO.type))
        contentValues.put(Contract.Catalog.DISCOUNT_TYPE, Integer.valueOf(catalogDTO.discountType))
        contentValues.put(Contract.Catalog.TAX_TYPE, Integer.valueOf(catalogDTO.taxType))
        contentValues.put(Contract.Catalog.TAX_LABEL, catalogDTO.taxLabel)
        contentValues.put("TAX_RATE", java.lang.Double.valueOf(catalogDTO.taxRate))
        contentValues.put("NAME", catalogDTO.catalogName)
        contentValues.put(Contract.Catalog.CREATED_AT, catalogDTO.creationDate)
        contentValues.put(Contract.Catalog.TERMS, Integer.valueOf(catalogDTO.terms))
        contentValues.put(Contract.Catalog.DUE_DATE, catalogDTO.dueDate)
        contentValues.put(Contract.Catalog.PO_NUMBER, catalogDTO.poNumber)
        contentValues.put("DISCOUNT", java.lang.Double.valueOf(catalogDTO.discount))
        contentValues.put(Contract.Catalog.PAID, java.lang.String.valueOf(catalogDTO.paidAmount))
        contentValues.put(
            Contract.Catalog.TOTAL_AMOUNT,
            java.lang.String.valueOf(catalogDTO.totalAmount)
        )
        contentValues.put(Contract.Catalog.SIGNED_DATE, catalogDTO.signedDate)
        contentValues.put("NOTES", catalogDTO.notes)
        contentValues.put(
            Contract.Catalog.ESTIMATE_STATUS,
            Integer.valueOf(catalogDTO.estimateStatus)
        )
        val update = myDb!!.update(
            Contract.Catalog.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + catalogDTO.id)
        )
        closeDB()
        try {
            DataProcessor.instance
                ?.notifyListeners(Gson().toJson(catalogDTO), MyConstants.ACTION_INVOICE_UPDATED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun updateInvoiceTax(taxDTO: TaxDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.TAX_TYPE, Integer.valueOf(taxDTO.taxType))
        contentValues.put(Contract.Catalog.TAX_LABEL, taxDTO.taxLabel)
        contentValues.put("TAX_RATE", java.lang.Double.valueOf(taxDTO.taxRate))
        val update = myDb!!.update(
            Contract.Catalog.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + taxDTO.catalogId)
        )
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(Gson().toJson(taxDTO), 103)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun deleteInvoice(j: Long) {
        openDb()
        myDb!!.delete(Contract.Catalog.TABLE_NAME, "_id = ?", arrayOf("" + j))
        closeDB()
        val catalogDTO = CatalogDTO()
        catalogDTO.id = j
        try {
            DataProcessor.instance
                ?.notifyListeners(Gson().toJson(catalogDTO), MyConstants.ACTION_INVOICE_DELETED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getInvoiceByClientName(str: String): ArrayList<CatalogDTO> {
        openDb()
        val rawQuery = myDb!!.rawQuery(
            "SELECT * FROM CATALOG c join CLIENTS cl on c.CLIENTS_ID = cl._id WHERE cl.NAME like '%$str%'",
            null
        )
        val arrayList = ArrayList<CatalogDTO>()
        while (rawQuery.moveToNext()) {
            val catalogDTO = CatalogDTO()
            catalogDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            catalogDTO.catalogName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
            catalogDTO.creationDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.CREATED_AT))
            catalogDTO.terms =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TERMS))
            catalogDTO.dueDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DUE_DATE))
            catalogDTO.poNumber =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PO_NUMBER))
            catalogDTO.totalAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .roundToInt()
            val singleClient = getSingleClient(
                rawQuery.getInt(
                    rawQuery.getColumnIndexOrThrow(
                        Contract.Catalog.CLIENTS_ID
                    )
                ).toLong()
            )
            if (singleClient.id > 0) {
                catalogDTO.clientDTO = singleClient
            } else {
                catalogDTO.clientDTO.clientName = "No client"
                catalogDTO.clientDTO.id = -1
            }
            arrayList.add(catalogDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    fun getInvoiceShipping(j: Long): InvoiceShippingDTO {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_INVOICE_SHIPPING, arrayOf("" + j))
        val invoiceShippingDTO = InvoiceShippingDTO()
        while (rawQuery.moveToNext()) {
            invoiceShippingDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            invoiceShippingDTO.amount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("AMOUNT")).toDouble()
            invoiceShippingDTO.shippingDate = rawQuery.getString(
                rawQuery.getColumnIndexOrThrow(
                    Contract.Shipping.SHIPPING_DATE
                )
            )
            invoiceShippingDTO.shipVia =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Shipping.SHIP_VIA))
            invoiceShippingDTO.tracking =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Shipping.TRACKING))
            invoiceShippingDTO.fob =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Shipping.FOB))
        }
        rawQuery.close()
        closeDB()
        return invoiceShippingDTO
    }

    fun saveInvoiceImage(imageDTO: ImageDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("CATALOG_ID", java.lang.Long.valueOf(imageDTO.catalogId))
        contentValues.put(Contract.CatalogImages.IMAGE_URL, imageDTO.imageUrl)
        contentValues.put("DESCRIPTION", imageDTO.description)
        contentValues.put(Contract.CatalogImages.ADDITIONAL_DETAILS, imageDTO.additionalDetails)
        val insert = myDb!!.insert(Contract.CatalogImages.TABLE_NAME, null, contentValues)
        imageDTO.id = insert
        closeDB()
        try {
            DataProcessor.instance
                ?.notifyListeners(Gson().toJson(imageDTO), MyConstants.ACTION_INVOICE_IMAGE_ADDED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insert
    }

    fun deleteInvoiceImage(imageDTO: ImageDTO): Int {
        openDb()
        val delete =
            myDb!!.delete(Contract.CatalogImages.TABLE_NAME, "_id = ?", arrayOf("" + imageDTO.id))
        closeDB()
        MyConstants.deleteFile(imageDTO.imageUrl)
        try {
            DataProcessor.instance
                ?.notifyListeners(Gson().toJson(imageDTO), MyConstants.ACTION_INVOICE_IMAGE_DELETED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return delete
    }

    fun updateInvoiceImage(str: String?, imageDTO: ImageDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.CatalogImages.IMAGE_URL, imageDTO.imageUrl)
        contentValues.put("DESCRIPTION", imageDTO.description)
        contentValues.put(Contract.CatalogImages.ADDITIONAL_DETAILS, imageDTO.additionalDetails)
        val update = myDb!!.update(
            Contract.CatalogImages.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + imageDTO.id)
        )
        closeDB()
        MyConstants.deleteFile(str)
        try {
            DataProcessor.instance
                ?.notifyListeners(Gson().toJson(imageDTO), MyConstants.ACTION_INVOICE_IMAGE_UPDATED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun getInvoiceItemImages(j: Long): ArrayList<ImageDTO> {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_CATALOG_IMAGES, arrayOf("" + j))
        val arrayList = ArrayList<ImageDTO>()
        while (rawQuery.moveToNext()) {
            val imageDTO = ImageDTO()
            imageDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            imageDTO.catalogId = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("CATALOG_ID"))
            imageDTO.imageUrl =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.CatalogImages.IMAGE_URL))
            imageDTO.description = rawQuery.getString(rawQuery.getColumnIndexOrThrow("DESCRIPTION"))
            imageDTO.additionalDetails =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.CatalogImages.ADDITIONAL_DETAILS))
            arrayList.add(imageDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    fun getInvoiceItems(j: Long): ArrayList<ItemAssociatedDTO> {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_INVOICE_ITEMS_ASSOCIATED, arrayOf("" + j))
        val arrayList = ArrayList<ItemAssociatedDTO>()
        while (rawQuery.moveToNext()) {
            val itemAssociatedDTO = ItemAssociatedDTO()
            itemAssociatedDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            itemAssociatedDTO.itemName =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.ItemsAssociated.ITEM_NAME))
            itemAssociatedDTO.description =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow("DESCRIPTION"))
            itemAssociatedDTO.unitCost = MyConstants.formatDecimal(

                rawQuery.getFloat(
                    rawQuery.getColumnIndexOrThrow("UNIT_COST")
                ).toDouble()

            )
            itemAssociatedDTO.taxAble = rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TAXABLE"))
            itemAssociatedDTO.catalogId = j
            itemAssociatedDTO.quantity =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.ItemsAssociated.QTY))
                    .toDouble()
            itemAssociatedDTO.taxRate =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")).toDouble()
            itemAssociatedDTO.discount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")).toDouble()
            arrayList.add(itemAssociatedDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    fun saveInvoiceItem(itemAssociatedDTO: ItemAssociatedDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("CATALOG_ID", java.lang.Long.valueOf(itemAssociatedDTO.catalogId))
        contentValues.put(Contract.ItemsAssociated.ITEM_NAME, itemAssociatedDTO.itemName)
        contentValues.put("DESCRIPTION", itemAssociatedDTO.description)
        contentValues.put(
            "UNIT_COST",
            java.lang.Double.valueOf(
                MyConstants.formatDecimal(
                    itemAssociatedDTO.unitCost
                )
            )
        )
        contentValues.put("TAXABLE", Integer.valueOf(itemAssociatedDTO.taxAble))
        contentValues.put(
            Contract.ItemsAssociated.QTY,
            java.lang.Double.valueOf(itemAssociatedDTO.quantity)
        )
        contentValues.put("TAX_RATE", java.lang.Double.valueOf(itemAssociatedDTO.taxRate))
        contentValues.put("DISCOUNT", java.lang.Double.valueOf(itemAssociatedDTO.discount))
        val insert = myDb!!.insert(Contract.ItemsAssociated.TABLE_NAME, null, contentValues)
        itemAssociatedDTO.id = insert
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(itemAssociatedDTO),
                MyConstants.ACTION_INVOICE_ITEM_ADDED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insert
    }

    fun updateInvoiceAmount(j: Long, d: Double): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.TOTAL_AMOUNT, java.lang.Double.valueOf(d))
        val update =
            myDb!!.update(Contract.Catalog.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + j))
        closeDB()
        return update
    }

    fun deleteInvoiceItem(j: Long): Int {
        openDb()
        val delete = myDb!!.delete(Contract.ItemsAssociated.TABLE_NAME, "_id = ?", arrayOf("" + j))
        closeDB()
        val itemAssociatedDTO = ItemAssociatedDTO()
        itemAssociatedDTO.id = j
        val quantity = itemAssociatedDTO.quantity * itemAssociatedDTO.unitCost
        itemAssociatedDTO.totalAmount =
            MyConstants.formatDecimal(quantity)
        updateInvoiceAmount(itemAssociatedDTO.catalogId, quantity)
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(itemAssociatedDTO),
                MyConstants.ACTION_INVOICE_ITEM_DELETED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return delete
    }

    fun updateInvoiceItem(itemAssociatedDTO: ItemAssociatedDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.ItemsAssociated.ITEM_NAME, itemAssociatedDTO.itemName)
        contentValues.put("DESCRIPTION", itemAssociatedDTO.description)
        contentValues.put(
            "UNIT_COST",
            java.lang.Double.valueOf(
                MyConstants.formatDecimal(
                    itemAssociatedDTO.unitCost
                )
            )
        )
        contentValues.put("TAXABLE", Integer.valueOf(itemAssociatedDTO.taxAble))
        contentValues.put(
            Contract.ItemsAssociated.QTY,
            java.lang.Double.valueOf(itemAssociatedDTO.quantity)
        )
        contentValues.put("TAX_RATE", java.lang.Double.valueOf(itemAssociatedDTO.taxRate))
        contentValues.put("DISCOUNT", java.lang.Double.valueOf(itemAssociatedDTO.discount))
        val update = myDb!!.update(
            Contract.ItemsAssociated.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + itemAssociatedDTO.id)
        )
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(itemAssociatedDTO),
                MyConstants.ACTION_INVOICE_ITEM_UPDATED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x011f  */
    fun calculateInvoiceBalance(j: Long): CatalogDTO {
        var d: Double
        val formatDecimal: Double
        val singleCatalog = getSingleCatalog(j)
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_INVOICE_ITEMS_ASSOCIATED, arrayOf("" + j))
        var d2 = 0.0
        var d3 = 0.0
        var d4 = 0.0
        var d5 = 0.0
        while (rawQuery.moveToNext()) {
            val d6 = rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")).toDouble()
            var d7 = rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("UNIT_COST"))
                .toDouble() * rawQuery.getFloat(
                rawQuery.getColumnIndexOrThrow(
                    Contract.ItemsAssociated.QTY
                )
            ).toDouble()
            if (singleCatalog.discountType == 1) {
                val d8 = d6 * d7 * 0.01
                d7 -= d8
                d3 += d8
            }
            d4 += d7
            if (rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TAXABLE")) == 1) {
                d2 += rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE"))
                    .toDouble() * d7 * 0.01
                d5 += d7
            }
        }
        rawQuery.close()
        closeDB()
        val discountType = singleCatalog.discountType
        if (discountType != 1) {
            if (discountType == 2) {
                d3 = singleCatalog.discount * d4 * 0.01
            } else if (discountType != 3) {
                d = d4
                d3 = 0.0
            } else {
                d3 = singleCatalog.discount
            }
            d = d4 - d3
        } else {
            d = d4
        }
        var amount = d + singleCatalog.invoiceShippingDTO.amount
        val taxType = singleCatalog.taxType
        if (taxType != 0) {
            if (taxType == 1) {
                d2 = (d5 - d3) * singleCatalog.taxRate * 0.01
                amount -= d2
            } else if (taxType != 2) {
                d2 = 0.0
            }
            singleCatalog.subTotalAmount = d4
            singleCatalog.discountAmount = d3
            singleCatalog.taxAmount = d2
            singleCatalog.totalAmount = amount.roundToInt()
            rawQuery.close()
            closeDB()
            updateInvoiceTotal(j, amount)
            formatDecimal =
                MyConstants.formatDecimal(singleCatalog.totalAmount.toDouble()) - MyConstants.formatDecimal(
                    singleCatalog.paidAmount.toDouble()
                )
            var d9 = 0.0
            if (formatDecimal == 0.0) {
                if (singleCatalog.totalAmount == 0 || singleCatalog.paidStatus == 2) {
                    d9 = 0.0
                } else {
                    singleCatalog.paidStatus = 2
                    updateInvoicePaidStatus(j, singleCatalog.paidStatus)
                    DataProcessor.instance?.notifyListeners(Gson().toJson(singleCatalog), 101)
                    return singleCatalog
                }
            }
            if (!(formatDecimal == d9 || singleCatalog.paidStatus == 1)) {
                singleCatalog.paidStatus = 1
                updateInvoicePaidStatus(j, singleCatalog.paidStatus)
            }
            DataProcessor.instance?.notifyListeners(Gson().toJson(singleCatalog), 101)
            return singleCatalog
        }
        d2 = (d5 - d3) * singleCatalog.taxRate * 0.01
        amount += d2
        singleCatalog.subTotalAmount = d4
        singleCatalog.discountAmount = d3
        singleCatalog.taxAmount = d2
        singleCatalog.totalAmount = amount.roundToInt()
        rawQuery.close()
        closeDB()
        updateInvoiceTotal(j, amount)
        formatDecimal =
            MyConstants.formatDecimal(singleCatalog.totalAmount.toDouble()) - MyConstants.formatDecimal(
                singleCatalog.paidAmount.toDouble()
            )
        val d92 = 0.0
        if (formatDecimal == 0.0) {
        }
        singleCatalog.paidStatus = 1
        updateInvoicePaidStatus(j, singleCatalog.paidStatus)
        try {
            DataProcessor.instance?.notifyListeners(Gson().toJson(singleCatalog), 101)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return singleCatalog
    }

    private fun updateInvoicePaidStatus(j: Long, i: Int): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.PAID_STATUS, Integer.valueOf(i))
        val update =
            myDb!!.update(Contract.Catalog.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + j))
        closeDB()
        return update
    }

    fun updateInvoiceTotal(j: Long, d: Double): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.TOTAL_AMOUNT, java.lang.Double.valueOf(d))
        val update =
            myDb!!.update(Contract.Catalog.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + j))
        closeDB()
        return update
    }

    fun addPayment(paymentDTO: PaymentDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("CATALOG_ID", java.lang.Long.valueOf(paymentDTO.catalogId))
        contentValues.put("AMOUNT", java.lang.Double.valueOf(paymentDTO.paidAmount))
        contentValues.put(Contract.Payments.DATE, paymentDTO.paymentDate)
        contentValues.put(Contract.Payments.METHOD, paymentDTO.paymentMethod)
        contentValues.put("NOTES", paymentDTO.paymentNotes)
        val insert = myDb!!.insert(Contract.Payments.TABLE_NAME, null, contentValues)
        closeDB()
        paymentDTO.id = insert
        updateInvoicePayment(paymentDTO.catalogId)
        notifyListenersForPaymentUpdate(paymentDTO)
        return insert
    }

    fun updatePayment(paymentDTO: PaymentDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("CATALOG_ID", java.lang.Long.valueOf(paymentDTO.catalogId))
        contentValues.put("AMOUNT", java.lang.Double.valueOf(paymentDTO.paidAmount))
        contentValues.put(Contract.Payments.DATE, paymentDTO.paymentDate)
        contentValues.put(Contract.Payments.METHOD, paymentDTO.paymentMethod)
        contentValues.put("NOTES", paymentDTO.paymentNotes)
        val update = myDb!!.update(
            Contract.Payments.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + paymentDTO.id)
        )
        closeDB()
        updateInvoicePayment(paymentDTO.catalogId)
        notifyListenersForPaymentUpdate(paymentDTO)
        return update.toLong()
    }

    fun deletePayment(paymentDTO: PaymentDTO) {
        openDb()
        myDb!!.delete(Contract.Payments.TABLE_NAME, "_id = ?", arrayOf("" + paymentDTO.id))
        closeDB()
        updateInvoicePayment(paymentDTO.catalogId)
        notifyListenersForPaymentUpdate(null)
    }

    fun notifyListenersForPaymentUpdate(paymentDTO: PaymentDTO?) {
        try {
            val gson = Gson()
            var str: String? = null
            if (paymentDTO != null) {
                str = gson.toJson(java.lang.Double.valueOf(paymentDTO.paidAmount))
            }
            DataProcessor.instance?.notifyListeners(str, 104)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPayments(j: Long): ArrayList<PaymentDTO> {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_INVOICE_PAYMENTS, arrayOf("" + j))
        val arrayList = ArrayList<PaymentDTO>()
        while (rawQuery.moveToNext()) {
            val paymentDTO = PaymentDTO()
            paymentDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            paymentDTO.catalogId = j
            paymentDTO.paidAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("AMOUNT")).toDouble()
            paymentDTO.paymentDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Payments.DATE))
            paymentDTO.paymentMethod =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Payments.METHOD))
            paymentDTO.paymentNotes = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES"))
            arrayList.add(paymentDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    fun getLastPaymentDate(j: Long): String {
        val payments = getPayments(j)
        return payments[payments.size - 1].paymentDate.toString()
    }

    fun getSinglePayment(j: Long): PaymentDTO {
        openDb()
        val query = myDb!!.query(
            Contract.Payments.TABLE_NAME,
            null,
            "_id=?",
            arrayOf("" + j),
            null,
            null,
            null
        )
        val paymentDTO = PaymentDTO()
        while (query.moveToNext()) {
            paymentDTO.id = query.getLong(query.getColumnIndexOrThrow("_id"))
            paymentDTO.catalogId = query.getLong(query.getColumnIndexOrThrow("CATALOG_ID"))
            paymentDTO.paidAmount = query.getFloat(query.getColumnIndexOrThrow("AMOUNT")).toDouble()
            paymentDTO.paymentDate =
                query.getString(query.getColumnIndexOrThrow(Contract.Payments.DATE))
            paymentDTO.paymentMethod =
                query.getString(query.getColumnIndexOrThrow(Contract.Payments.METHOD))
            paymentDTO.paymentNotes = query.getString(query.getColumnIndexOrThrow("NOTES"))
        }
        query.close()
        closeDB()
        return paymentDTO
    }

    fun updateInvoicePayment(j: Long): Int {
        val payments = getPayments(j)
        var d = 0.0
        if (payments != null && payments.size > 0) {
            for (i in payments.indices) {
                d += payments[i].paidAmount
            }
        }
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.PAID, java.lang.Double.valueOf(d))
        val update =
            myDb!!.update(Contract.Catalog.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + j))
        closeDB()
        return update
    }

    fun updateInvoiceSignature(signedDTO: SignedDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.SIGNED_DATE, signedDTO.signedDate)
        contentValues.put(Contract.Catalog.SIGNED_URL, signedDTO.signedUrl)
        val update = myDb!!.update(
            Contract.Catalog.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + signedDTO.catalogId)
        )
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(Gson().toJson(signedDTO), 105)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun updateInvoiceDiscount(discountDTO: DiscountDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.DISCOUNT_TYPE, Integer.valueOf(discountDTO.discountType))
        contentValues.put("DISCOUNT", java.lang.Double.valueOf(discountDTO.discountAmount))
        val update = myDb!!.update(
            Contract.Catalog.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + discountDTO.catalogId)
        )
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(Gson().toJson(discountDTO), 102)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun updateInvoiceNotes(j: Long, str: String?): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NOTES", str)
        val update =
            myDb!!.update(Contract.Catalog.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + j))
        closeDB()
        return update
    }

    fun saveInvoiceShipping(invoiceShippingDTO: InvoiceShippingDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("CATALOG_ID", java.lang.Long.valueOf(invoiceShippingDTO.catalogId))
        contentValues.put("AMOUNT", java.lang.Double.valueOf(invoiceShippingDTO.amount))
        contentValues.put(Contract.Shipping.SHIPPING_DATE, invoiceShippingDTO.shippingDate)
        contentValues.put(Contract.Shipping.SHIP_VIA, invoiceShippingDTO.shipVia)
        contentValues.put(Contract.Shipping.TRACKING, invoiceShippingDTO.tracking)
        contentValues.put(Contract.Shipping.FOB, invoiceShippingDTO.fob)
        val insert = myDb!!.insert(Contract.Shipping.TABLE_NAME, null, contentValues)
        invoiceShippingDTO.id = insert
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(invoiceShippingDTO),
                MyConstants.ACTION_INVOICE_SHIPPING_ADDED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insert
    }

    fun updateInvoiceShipping(invoiceShippingDTO: InvoiceShippingDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("AMOUNT", java.lang.Double.valueOf(invoiceShippingDTO.amount))
        contentValues.put(Contract.Shipping.SHIPPING_DATE, invoiceShippingDTO.shippingDate)
        contentValues.put(Contract.Shipping.SHIP_VIA, invoiceShippingDTO.shipVia)
        contentValues.put(Contract.Shipping.TRACKING, invoiceShippingDTO.tracking)
        contentValues.put(Contract.Shipping.FOB, invoiceShippingDTO.fob)
        val update = myDb!!.update(
            Contract.Shipping.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + invoiceShippingDTO.id)
        )
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(invoiceShippingDTO),
                MyConstants.ACTION_INVOICE_SHIPPING_UPDATED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun deleteInvoiceShipping(j: Long) {
        openDb()
        myDb!!.delete(Contract.Shipping.TABLE_NAME, "CATALOG_ID = ?", arrayOf("" + j))
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(java.lang.Long.valueOf(j)),
                MyConstants.ACTION_INVOICE_SHIPPING_DELETED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUnPaidInvoices(i: Int): ArrayList<CatalogDTO> {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_PAID_FROM_CATALOG, arrayOf("" + i, "0"))
        val arrayList = ArrayList<CatalogDTO>()
        while (rawQuery.moveToNext()) {
            val catalogDTO = CatalogDTO()
            catalogDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            catalogDTO.type =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TYPE))
            catalogDTO.catalogName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
            catalogDTO.creationDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.CREATED_AT))
            catalogDTO.terms =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TERMS))
            catalogDTO.dueDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DUE_DATE))
            catalogDTO.poNumber =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PO_NUMBER))
            catalogDTO.discountType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DISCOUNT_TYPE))
            catalogDTO.discount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")).toDouble()
            catalogDTO.taxType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_TYPE))
            catalogDTO.taxLabel =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_LABEL))
            catalogDTO.taxRate =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")).toDouble()
            catalogDTO.paidAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID))
                    .roundToInt()
            catalogDTO.paidStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID_STATUS))
            catalogDTO.estimateStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.ESTIMATE_STATUS))
            catalogDTO.totalAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .roundToInt()
            catalogDTO.signedDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_DATE))
//            catalogDTO.signedUrl =
//                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_URL))
            catalogDTO.notes = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES"))
            val singleClient = getSingleClient(
                rawQuery.getInt(
                    rawQuery.getColumnIndexOrThrow(
                        Contract.Catalog.CLIENTS_ID
                    )
                ).toLong()
            )
            if (singleClient.id > 0) {
                catalogDTO.clientDTO = singleClient
            }
            catalogDTO.invoiceShippingDTO = getInvoiceShipping(catalogDTO.id)
            arrayList.add(catalogDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    fun getCatalogsByClient(j: Long, i: Int, i2: Int): ArrayList<CatalogDTO> {
        openDb()
        val rawQuery =
            myDb!!.rawQuery(Query.SELECT_CATALOGS_BY_CLIENT, arrayOf("" + i, "" + i2, "" + j))
        val arrayList = ArrayList<CatalogDTO>()
        while (rawQuery.moveToNext()) {
            val catalogDTO = CatalogDTO()
            catalogDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            catalogDTO.type =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TYPE))
            catalogDTO.catalogName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
            catalogDTO.creationDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.CREATED_AT))
            catalogDTO.terms =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TERMS))
            catalogDTO.dueDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DUE_DATE))
            catalogDTO.poNumber =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PO_NUMBER))
            catalogDTO.discountType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.DISCOUNT_TYPE))
            catalogDTO.discount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("DISCOUNT")).toDouble()
            catalogDTO.taxType =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_TYPE))
            catalogDTO.taxLabel =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TAX_LABEL))
            catalogDTO.taxRate =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow("TAX_RATE")).toDouble()
            catalogDTO.paidAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID))
                    .roundToInt()
            catalogDTO.paidStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID_STATUS))
            catalogDTO.estimateStatus =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow(Contract.Catalog.ESTIMATE_STATUS))
            catalogDTO.totalAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .roundToInt()
            catalogDTO.signedDate =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_DATE))
//            catalogDTO.signedUrl =
//                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.SIGNED_URL))
            catalogDTO.notes = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NOTES"))
            val singleClient = getSingleClient(
                rawQuery.getInt(
                    rawQuery.getColumnIndexOrThrow(
                        Contract.Catalog.CLIENTS_ID
                    )
                ).toLong()
            )
            if (singleClient.id > 0) {
                catalogDTO.clientDTO = singleClient
            }
            catalogDTO.invoiceShippingDTO = getInvoiceShipping(catalogDTO.id)
            arrayList.add(catalogDTO)
        }
        rawQuery.close()
        closeDB()
        return arrayList
    }

    val invoiceHistories: ArrayList<String>
        get() {
            val arrayList = ArrayList<String>()
            try {
                openDb()
                val rawQuery = myDb!!.rawQuery(Query.SELECT_CREATION_DATE_FROM_INVOICES, null)
                while (rawQuery.moveToNext()) {
                    arrayList.add(rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Catalog.CREATED_AT)))
                }
                rawQuery.close()
                closeDB()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return arrayList
        }

    fun updateInvoiceClient(clientDTO: ClientDTO?) {
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(clientDTO),
                MyConstants.ACTION_INVOICE_CLIENT_UPDATED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addInvoiceClient(clientDTO: ClientDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.CLIENTS_ID, java.lang.Long.valueOf(clientDTO.id))
        val update = myDb!!.update(
            Contract.Catalog.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + clientDTO.catalogId)
        )
        closeDB()
        try {
            DataProcessor.instance
                ?.notifyListeners(Gson().toJson(clientDTO), MyConstants.ACTION_INVOICE_CLIENT_ADDED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun deleteInvoiceClient(j: Long, j2: Long): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put(Contract.Catalog.CLIENTS_ID, java.lang.Long.valueOf(j2))
        val update =
            myDb!!.update(Contract.Catalog.TABLE_NAME, contentValues, "_id = ?", arrayOf("" + j))
        closeDB()
        try {
            DataProcessor.instance?.notifyListeners(
                Gson().toJson(java.lang.Long.valueOf(j)),
                MyConstants.ACTION_INVOICE_CLIENT_DELETED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return update
    }

    fun deleteClient(j: Long) {
        openDb()
        myDb!!.delete(Contract.Clients.TABLE_NAME, "_id = ?", arrayOf("" + j))
        closeDB()
    }

    fun saveClient(clientDTO: ClientDTO): Long {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", clientDTO.clientName)
        contentValues.put("EMAIL", clientDTO.emailAddress)
        contentValues.put("MOBILE", clientDTO.mobileNo)
        contentValues.put("PHONE", clientDTO.phoneNo)
        contentValues.put("FAX", clientDTO.faxNo)
        contentValues.put(Contract.Clients.ADDRESS_CONTACT, clientDTO.contactAdress)
        contentValues.put("ADDRESS_LINE_1", clientDTO.addressLine1)
        contentValues.put("ADDRESS_LINE_2", clientDTO.addressLine2)
        contentValues.put("ADDRESS_LINE_3", clientDTO.addressLine3)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_NAME, clientDTO.shippingAddress)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_1, clientDTO.shippingLine1)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_2, clientDTO.shippingLine2)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_3, clientDTO.shippingLine3)
        val insert = myDb!!.insert(Contract.Clients.TABLE_NAME, null, contentValues)
        closeDB()
        return insert
    }

    fun updateClient(clientDTO: ClientDTO): Int {
        openDb()
        val contentValues = ContentValues()
        contentValues.put("NAME", clientDTO.clientName)
        contentValues.put("EMAIL", clientDTO.emailAddress)
        contentValues.put("MOBILE", clientDTO.mobileNo)
        contentValues.put("PHONE", clientDTO.phoneNo)
        contentValues.put("FAX", clientDTO.faxNo)
        contentValues.put(Contract.Clients.ADDRESS_CONTACT, clientDTO.contactAdress)
        contentValues.put("ADDRESS_LINE_1", clientDTO.addressLine1)
        contentValues.put("ADDRESS_LINE_2", clientDTO.addressLine2)
        contentValues.put("ADDRESS_LINE_3", clientDTO.addressLine3)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_NAME, clientDTO.shippingAddress)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_1, clientDTO.shippingLine1)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_2, clientDTO.shippingLine2)
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_3, clientDTO.shippingLine3)
        val update = myDb!!.update(
            Contract.Clients.TABLE_NAME,
            contentValues,
            "_id = ?",
            arrayOf("" + clientDTO.id)
        )
        closeDB()
        return update
    }

    fun getSingleClient(j: Long): ClientDTO {
        openDb()
        val rawQuery = myDb!!.rawQuery(Query.SELECT_SINGLE_CLIENT, arrayOf("" + j))
        val clientDTO = ClientDTO()
        while (rawQuery.moveToNext()) {
            clientDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
            clientDTO.clientName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
            clientDTO.emailAddress = rawQuery.getString(rawQuery.getColumnIndexOrThrow("EMAIL"))
            clientDTO.mobileNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("MOBILE"))
            clientDTO.phoneNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("PHONE"))
            clientDTO.faxNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("FAX"))
            clientDTO.contactAdress =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.ADDRESS_CONTACT))
            clientDTO.addressLine1 =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_1"))
            clientDTO.addressLine2 =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_2"))
            clientDTO.addressLine3 =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_3"))
            clientDTO.shippingAddress =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_NAME))
            clientDTO.shippingLine1 =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_LINE_1))
            clientDTO.shippingLine2 =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_LINE_2))
            clientDTO.shippingLine3 =
                rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_LINE_3))
        }
        rawQuery.close()
        closeDB()
        return clientDTO
    }

    val clientList: ArrayList<ClientDTO>
        get() {
            openDb()
            val rawQuery = myDb!!.rawQuery(Query.SELECT_ALL_FROM_CLIENTS, null)
            val arrayList = ArrayList<ClientDTO>()
            while (rawQuery.moveToNext()) {
                val clientDTO = ClientDTO()
                clientDTO.id = rawQuery.getLong(rawQuery.getColumnIndexOrThrow("_id"))
                clientDTO.clientName = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
                clientDTO.emailAddress = rawQuery.getString(rawQuery.getColumnIndexOrThrow("EMAIL"))
                clientDTO.mobileNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("MOBILE"))
                clientDTO.phoneNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("PHONE"))
                clientDTO.faxNo = rawQuery.getString(rawQuery.getColumnIndexOrThrow("FAX"))
                clientDTO.contactAdress =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.ADDRESS_CONTACT))
                clientDTO.addressLine1 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_1"))
                clientDTO.addressLine2 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_2"))
                clientDTO.addressLine3 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow("ADDRESS_LINE_3"))
                clientDTO.shippingAddress =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_NAME))
                clientDTO.shippingLine1 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_LINE_1))
                clientDTO.shippingLine2 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_LINE_2))
                clientDTO.shippingLine3 =
                    rawQuery.getString(rawQuery.getColumnIndexOrThrow(Contract.Clients.SHIPPING_ADDRESS_LINE_3))
                getInvoiceByClient(clientDTO)
                getEstimateByClient(clientDTO)
                arrayList.add(clientDTO)
            }
            rawQuery.close()
            closeDB()
            return arrayList
        }

    fun getInvoiceByClient(clientDTO: ClientDTO): ClientDTO {
        val rawQuery =
            myDb!!.rawQuery(Query.SELECT_CATALOG_BY_CLIENT, arrayOf("0", "" + clientDTO.id))
        while (rawQuery.moveToNext()) {
            clientDTO.totalAmount =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .toDouble()
            clientDTO.dueAmount =
                clientDTO.totalAmount - rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.PAID))
                    .toDouble()
            clientDTO.totalInvoice =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TOTAL_CATALOG"))
        }
        rawQuery.close()
        return clientDTO
    }

    fun getEstimateByClient(clientDTO: ClientDTO): ClientDTO {
        val rawQuery =
            myDb!!.rawQuery(Query.SELECT_CATALOG_BY_CLIENT, arrayOf("1", "" + clientDTO.id))
        while (rawQuery.moveToNext()) {
            clientDTO.totalAmountEstimate =
                rawQuery.getFloat(rawQuery.getColumnIndexOrThrow(Contract.Catalog.TOTAL_AMOUNT))
                    .toDouble()
            clientDTO.totalEstimate =
                rawQuery.getInt(rawQuery.getColumnIndexOrThrow("TOTAL_CATALOG"))
        }
        rawQuery.close()
        return clientDTO
    }

    val businessName: String
        get() {
            var str = ""
            try {
                openDb()
                val rawQuery = myDb!!.rawQuery(Query.SELECT_ALL_FROM_BUSINESS_INFORMATION, null)
                while (rawQuery.moveToNext()) {
                    str = rawQuery.getString(rawQuery.getColumnIndexOrThrow("NAME"))
                }
                rawQuery.close()
                closeDB()
            } catch (unused: Exception) {
            }
            return str
        }

    fun createDuplicate(catalogDTO: CatalogDTO): Long {
        val id = catalogDTO.id
        val saveInvoice = saveInvoice(catalogDTO)
        catalogDTO.id = saveInvoice
        if (saveInvoice > 0) {
            val invoiceItems = instance!!.getInvoiceItems(id)
            for (i in invoiceItems.indices) {
                invoiceItems[i].catalogId = saveInvoice
                saveInvoiceItem(invoiceItems[i])
            }
            val invoiceShipping = getInvoiceShipping(id)
            if (invoiceShipping.id > 0) {
                invoiceShipping.catalogId = saveInvoice
                saveInvoiceShipping(invoiceShipping)
            }
            val payments = getPayments(id)
            for (i2 in payments.indices) {
                payments[i2].catalogId = saveInvoice
                addPayment(payments[i2])
            }
            val signedUrl = catalogDTO.signedUrl
            if (!TextUtils.isEmpty(signedUrl)) {
                val file = File(signedUrl)
                if (file.exists()) {
                    val str = MyConstants.rootDirectory + File.separator + "signature"
                    val str2 =
                        str + File.separator + "Signature_" + Timestamp(System.currentTimeMillis()) + ".jpg"
                    try {
                        replicateFile(file, File(str2))
                        catalogDTO.signedUrl = str2
                        val signedDTO = SignedDTO()
                        signedDTO.catalogId = saveInvoice
                        signedDTO.signedDate = catalogDTO.signedDate.toString()
                        signedDTO.signedUrl = str2
                        updateInvoiceSignature(signedDTO)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            val invoiceItemImages = getInvoiceItemImages(id)
            for (i3 in invoiceItemImages.indices) {
                invoiceItemImages[i3].catalogId = saveInvoice
                val imageUrl = invoiceItemImages[i3].imageUrl
                if (!TextUtils.isEmpty(imageUrl)) {
                    val file2 = File(imageUrl)
                    if (file2.exists()) {
                        val timestamp = Timestamp(System.currentTimeMillis())
                        val str3 =
                            MyConstants.rootDirectory + File.separator + timestamp + ".jpg"
                        try {
                            replicateFile(file2, File(str3))
                            invoiceItemImages[i3].imageUrl = str3
                            saveInvoiceImage(invoiceItemImages[i3])
                        } catch (e2: IOException) {
                            e2.printStackTrace()
                        }
                    }
                }
            }
        }
        return saveInvoice
    }

    @Throws(IOException::class)
    private fun replicateFile(file: File, file2: File) {
        val channel = FileInputStream(file).channel
        val channel2 = FileOutputStream(file2).channel
        if (!(channel2 == null || channel == null)) {
            channel2.transferFrom(channel, 0, channel.size())
        }
        channel?.close()
        channel2?.close()
    }

    @Throws(IOException::class)
    private fun replicateFile(str: String) {
        if (!TextUtils.isEmpty(str)) {
            val file = File(str)
            if (file.exists()) {
                val str2 = MyConstants.rootDirectory + File.separator + "signature"
                val file2 =
                    File(str2 + File.separator + "Signature_" + Timestamp(System.currentTimeMillis()) + ".jpg")
                val channel = FileInputStream(file).channel
                val channel2 = FileOutputStream(file2).channel
                if (!(channel2 == null || channel == null)) {
                    channel2.transferFrom(channel, 0, channel.size())
                }
                channel?.close()
                channel2?.close()
            }
        }
    }

    fun truncateDB(context: Context?) {
        openDb()
        myDb!!.delete(Contract.Settings.TABLE_NAME, null, null)
        myDb!!.delete(Contract.ItemsAssociated.TABLE_NAME, null, null)
        myDb!!.delete(Contract.Items.TABLE_NAME, null, null)
        myDb!!.delete(Contract.Shipping.TABLE_NAME, null, null)
        myDb!!.delete(Contract.Payments.TABLE_NAME, null, null)
        myDb!!.delete(Contract.CatalogImages.TABLE_NAME, null, null)
        myDb!!.delete(Contract.Clients.TABLE_NAME, null, null)
        myDb!!.delete(Contract.BusinessInformation.TABLE_NAME, null, null)
        myDb!!.delete(Contract.Catalog.TABLE_NAME, null, null)
        DatabaseHelper(context).populateSettingsTable(myDb!!)
        closeDB()
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseOpenClose
    override fun openDb() {
        if (myDb == null) {
            myDb = getInstance()!!.openDatabase()
        }
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseOpenClose
    override fun closeDB() {
        if (myDb != null) {
            getInstance()!!.closeDatabase()
            myDb = null
        }
    }

    companion object {
        private var mInstance: LoadDatabase? = null

        @JvmStatic
        @get:Synchronized
        val instance: LoadDatabase?
            get() {
                var loadDatabase: LoadDatabase?
                synchronized(LoadDatabase::class.java) {
                    synchronized(LoadDatabase::class.java) {
                        if (mInstance == null) {
                            mInstance = LoadDatabase()
                        }
                        loadDatabase = mInstance
                    }
                    return loadDatabase
                }
            }
    }
}