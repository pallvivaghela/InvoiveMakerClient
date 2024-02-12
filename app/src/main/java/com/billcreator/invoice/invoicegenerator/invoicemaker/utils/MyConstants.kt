package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.CatalogDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Listener.ConfirmListener
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object MyConstants {
    const val ACTION_BUSINESS_INFORMATION_ADDED = 10001
    const val ACTION_BUSINESS_INFORMATION_DELETED = 10003
    const val ACTION_BUSINESS_INFORMATION_UPDATED = 10002
    const val ACTION_INVOICE_ADDED = 2001
    const val ACTION_INVOICE_CLIENT_ADDED = 4001
    const val ACTION_INVOICE_CLIENT_DELETED = 4003
    const val ACTION_INVOICE_CLIENT_UPDATED = 4002
    const val ACTION_INVOICE_DELETED = 2003
    const val ACTION_INVOICE_IMAGE_ADDED = 3004
    const val ACTION_INVOICE_IMAGE_DELETED = 3006
    const val ACTION_INVOICE_IMAGE_UPDATED = 3005
    const val ACTION_INVOICE_ITEM_ADDED = 3001
    const val ACTION_INVOICE_ITEM_DELETED = 3003
    const val ACTION_INVOICE_ITEM_UPDATED = 3002
    const val ACTION_INVOICE_LIST_UPDATE = 2001
    const val ACTION_INVOICE_SHIPPING_ADDED = 5001
    const val ACTION_INVOICE_SHIPPING_DELETED = 5003
    const val ACTION_INVOICE_SHIPPING_UPDATED = 5002
    const val ACTION_INVOICE_UPDATED = 2002
    const val ACTION_PASSCODE_ADDED = 666
    const val ACTION_SETTINGS_UPDATED = 321
    const val ACTION_UPDATE_DISCOUNT_AMOUNT = 102
    const val ACTION_UPDATE_INVOICE_BALANCE = 101
    const val ACTION_UPDATE_INVOICE_SIGNATURE = 105
    const val ACTION_UPDATE_PAID_AMOUNT = 104
    const val ACTION_UPDATE_TAX_AMOUNT = 103
    var BUSINESS_DTO = "business_dto"
    var CALLER_ACTIVITY = "caller_activity"

    @JvmField
    var CATALOG_DTO = "catalog_dto"

    @JvmField
    var CATALOG_TYPE = 0
    const val CATALOG_TYPE_ESTIMATE = 1
    const val CATALOG_TYPE_INVOICE = 0
    var CLIENT_DTO = "client_dto"
    const val CLOSED_ESTIMATE = 2
    const val DEFAULT_CURRENCY_FORMAT = 102
    const val DEFAULT_DATE_FORMAT = 0
    var DISCOUNT_AMOUNT = "discount_amount"
    var DISCOUNT_AMOUNT_SUBTOTAL = "discount_amount_subtotal"
    var DISCOUNT_AMOUNT_TOTAL = "discount_amount_total"
    var DISCOUNT_TYPE = "discount_type"
    const val DISCOUNT_TYPE_FLAT_AMOUNT = 3
    const val DISCOUNT_TYPE_NONE = 0
    const val DISCOUNT_TYPE_PERCENTAGE = 2
    const val DISCOUNT_TYPE_PER_ITEM = 1
    var DUPLICATE_ENTRY_FOR = 1
    var FROM_CATALOG = "FROM_CATALOG"
    var IMAGE_DTO = "image_dto"
    var IMAGE_URL = "image_url"
    var IS_INVOICE = "IS_INVOICE"
    var ITEM_ASSOCIATED_DTO = "item_associated_dto"
    const val OPEN_ESTIMATE = 1
    var OPERATION_TYPE = "operation_type"
    const val OUTSTANDING_INVOICE = 1
    var PAID_AMOUNT = "paid_amount"
    const val PAID_INVOICE = 2
    var PAYMENT_DTO = "payment_dto"
    const val PICK_IMAGE = 103
    const val REQUEST_CAMERA = 102
    const val REQUEST_READ_EXTERNAL_STORAGE = 102
    const val REQUEST_WRITE_EXTERNAL_STORAGE = 101
    var SHIPPING_DTO = "shipping_dto"
    var SIGNED_DATE = "signed_date"
    var SIGNED_URL = "signed_url"
    var TAG = "MyConstants"
    var TAX_LABEL = "tax_label"
    var TAX_RATE = "tax_rate"
    var TAX_TYPE = "tax_type"
    const val TAX_TYPE_DEDUCTED = 1
    const val TAX_TYPE_NONE = 3
    const val TAX_TYPE_ON_THE_TOTAL = 0
    const val TAX_TYPE_PER_ITEM = 2
    var TOTAL_AMOUNT = "total_amount"
    const val TYPE_PAYMENT_ADD = 0
    const val TYPE_PAYMENT_EDIT = 1

    @JvmField
    var createDuplicateEntry = false
    var invoiceCount = 1
    val sdf: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)

    @JvmStatic
    fun formatDate(context: Context, j: Long, i: Int): String {
        return SimpleDateFormat(
            context.resources.getStringArray(R.array.date_format_titles)[i],
            Locale.ENGLISH
        ).format(java.lang.Long.valueOf(j))
    }

    @JvmStatic
    fun formatCurrency(context: Context, i: Int): String {
        return context.resources.getStringArray(R.array.currency_symbols)[i]
    }

    @JvmStatic
    val invoiceName: String
        get() = if (CATALOG_TYPE == 1) {
            "ESTIMATE" + String.format(
                "%d",
                Integer.valueOf(invoiceCount)
            )
        } else "INVOICE-" + String.format(
            "%d",
            Integer.valueOf(invoiceCount)
        )

    @JvmStatic
    fun createNewInvoice(): Long {
        val catalogDTO = CatalogDTO()
        catalogDTO.catalogName = invoiceName
        catalogDTO.creationDate = Calendar.getInstance().timeInMillis.toString()
        catalogDTO.terms = 1
        catalogDTO.discountType = 0
        catalogDTO.taxType = 3
        catalogDTO.paidStatus = 1
        invoiceCount++
        return instance!!.saveInvoice(catalogDTO)
    }

    val rootDirectory: String
        get() = try {
            val str = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + File.separator + "invoice"
            try {
                val file = File(str)
                if (!file.exists()) {
                    file.mkdirs()
                }
                str
            } catch (e: Exception) {
                e.printStackTrace()
                str
            } catch (unused: Throwable) {
                str
            }
        } catch (e2: Exception) {
            e2.printStackTrace()
            ""
        }

    fun loadImage(context: Context?, str: String?, imageView: ImageView?) {
        Glide.with(context!!).load(str).thumbnail(0.5f)
            .transition(DrawableTransitionOptions().crossFade()).apply(
                (RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE) as BaseRequestOptions<*>)
            ).into(imageView!!)
    }

    fun loadImage(context: Context?, uri: Uri?, imageView: ImageView?) {
        Glide.with(context!!).load(uri).thumbnail(0.5f)
            .transition(DrawableTransitionOptions().crossFade()).apply(
                (RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE) as BaseRequestOptions<*>)
            ).into(imageView!!)
    }

    fun showConfirmDialog(
        context: Context?,
        str: String?,
        str2: String?,
        confirmListener: ConfirmListener?
    ) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(1)
        dialog.setContentView(R.layout.confirm_dialog_layout)
        (dialog.findViewById<View>(R.id.title) as TextView).text = str
        (dialog.findViewById<View>(R.id.message) as TextView).text = str2
        dialog.findViewById<View>(R.id.ok)
            .setOnClickListener(`MyConstants$$ExternalSyntheticLambda0`(confirmListener!!, dialog))
        dialog.findViewById<View>(R.id.cancel)
            .setOnClickListener { //                ConfirmListener.this.cancel();
                dialog.dismiss()
            }
        dialog.show()
    }

    @JvmStatic
    fun `lambda$showConfirmDialog$0`(
        confirmListener: ConfirmListener,
        dialog: Dialog,
        view: View?
    ) {
        confirmListener.ok()
        dialog.dismiss()
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri?): String {
        val query = context.contentResolver.query(uri!!, null, null, null, null)
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    val string = query.getString(query.getColumnIndex("_display_name"))
                    query.close()
                    return string
                }
            } catch (unused: Exception) {
                query.close()
                return ""
            } catch (unused2: Throwable) {
                query.close()
                return ""
            }
        }
        query!!.close()
        return ""
    }

    fun deleteFile(str: String?): String {
        try {
            val file = File(str)
            if (file.exists()) {
                file.delete()
            }
        } catch (unused: Exception) {
        }
        return ""
    }

    @JvmStatic
    fun formatDecimal(d: Double): Double {
        return BigDecimal(d.toString()).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}











