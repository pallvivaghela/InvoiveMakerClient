package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddClientActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddItemActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddPhotoActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.DiscountActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceInfoActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoicePreviewActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.PaymentActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.PaymentOptionActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SearchClientActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.ShippingInfoActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SignatureActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.TaxActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ItemAssociatedAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ItemImageAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.*
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.createNewInvoice
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.formatCurrency
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.formatDate
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.formatDecimal
import com.google.gson.Gson
import com.itextpdf.text.pdf.security.SecurityConstants
import java.util.*
import kotlin.collections.ArrayList

@SuppressWarnings("all")
class EditInvoiceFragment : Fragment(), View.OnClickListener, ModelChangeListener {
    private var businessDTO: BusinessDTO? = null
    private  var businessName: TextView? = null
    private var catalogDTO: CatalogDTO? = null
    private var clientName: TextView? = null
    private var creationDate: TextView? = null
    private var currencySign: String? = null
    private var discountText: TextView? = null
    private var dueAmount: TextView? = null
    private var dueInfo: TextView? = null
    private var imageDTO: ImageDTO? = null
    private var imageDTOS: ArrayList<ImageDTO>? = null
    private var invoiceDiscount: TextView? = null
    private var invoiceItemsRv: RecyclerView? = null
    private var invoiceName: TextView? = null
    private var invoiceNotes: EditText? = null
    private var itemAssociatedAdapter: ItemAssociatedAdapter? = null
    private var itemAssociatedDTO: ItemAssociatedDTO? = null
    private var itemAssociatedDTOS: ArrayList<ItemAssociatedDTO>? = null
    private var itemImageAdapter: ItemImageAdapter? = null
    private var itemImagesRv: RecyclerView? = null
    private var mActivity: Activity? = null
    private var nestedScrollView: NestedScrollView? = null
    private var paidAmount: TextView? = null
    private var paymentOption: TextView? = null
    private var paymentOptionText: TextView? = null
    private var settingsDTO: SettingsDTO? = null
    private var shippingAmount: TextView? = null
    private var signedDate: TextView? = null
    private var subtotalAmount: TextView? = null
    private var taxAmount: TextView? = null
    private var taxText: TextView? = null
    private var totalAmount: TextView? = null
    private var totalCost: TextView? = null
    private var unitCostQuantity: TextView? = null
    private var viewv: View? = null

    private inner class AsyncCaller : AsyncTask<Void?, Void?, Boolean>() {
        override fun doInBackground(vararg p0: Void?): Boolean {
            return try {
                loadItemImages()
                loadInvoiceItems()
                true
            } catch (unused: Exception) {
                false
            }
        }
        @SuppressLint("WrongConstant")
        public override fun onPostExecute(bool: Boolean) {
            super.onPostExecute(bool)
            if (bool) {
                if (itemAssociatedDTOS!!.size > 0) {
                    invoiceItemsRv!!.visibility = 0
                    itemAssociatedAdapter!!.notifyDataSetChanged()
                }
                if (imageDTOS!!.size > 0) {
                    itemImagesRv!!.visibility = 0
                    itemImageAdapter!!.notifyDataSetChanged()
                }
                LoadDatabase.instance!!.calculateInvoiceBalance(catalogDTO!!.id)
                updateInvoiceInfo()
            }
        }


    }

    // androidx.fragment.app.Fragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    // androidx.fragment.app.Fragment
    @SuppressLint("WrongConstant")
    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        if (viewv == null) {
            viewv = layoutInflater.inflate(R.layout.edit_invoice_layout, viewGroup, false)
            DataProcessor.instance!!.addChangeListener(this)
            itemAssociatedDTOS = ArrayList()
            imageDTOS = ArrayList()
            intentData
            businessDTO = LoadDatabase.instance!!.businessInformation
            this.settingsDTO = SettingsDTO.settingsDTO
            initLayout()
            if (catalogDTO!!.id > 0) {
                AsyncCaller().execute(*arrayOfNulls<Void>(0))
            }
            val makeBusinessString = makeBusinessString()
            if (makeBusinessString.length > 0) {
                val textView = paymentOptionText
                textView!!.text = mActivity!!.resources.getString(R.string.payment_options) + ":"
                paymentOption!!.text = makeBusinessString
                paymentOption!!.visibility = 0
            } else {
                paymentOption!!.text = ""
                paymentOption!!.visibility = 8
            }
        }
        return viewv
    }

    private val intentData: Unit
        @SuppressLint("UseRequireInsteadOfGet") private get() {
            val catalogDTO2 = arguments!!.getSerializable(MyConstants.CATALOG_DTO) as CatalogDTO?
            catalogDTO = catalogDTO2
            if (catalogDTO2 == null) {
                catalogDTO = CatalogDTO()
            }
        }

    fun makeBusinessString(): String {
        return if (!TextUtils.isEmpty(businessDTO!!.bankInformation)) {
            "" + "Bank transfer"
        } else if (!TextUtils.isEmpty(businessDTO!!.paypalAddress)) {
            "" + "PayPal"
        } else if (!TextUtils.isEmpty(businessDTO!!.checkInformation)) {
            "" + "Check"
        } else if (TextUtils.isEmpty(businessDTO!!.otherPaymentInformation)) {
            "".replace(", $".toRegex(), "")
        } else {
            "" + "Other"
        }
    }

    private fun loadInvoiceItems() {
        itemAssociatedDTOS!!.clear()
        val invoiceItems: ArrayList<ItemAssociatedDTO> = LoadDatabase.instance!!.getInvoiceItems(
            catalogDTO!!.id
        )
        if (invoiceItems != null && invoiceItems.size > 0) {
            itemAssociatedDTOS!!.addAll(invoiceItems)
        }
    }

    private fun loadItemImages() {
        imageDTOS!!.clear()
        val invoiceItemImages: ArrayList<ImageDTO> = LoadDatabase.instance!!.getInvoiceItemImages(
            catalogDTO!!.id
        )
        if (invoiceItemImages != null && invoiceItemImages.size > 0) {
            imageDTOS!!.addAll(invoiceItemImages)
        }
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        this.invoiceName = viewv!!.findViewById<View>(R.id.invoice_name) as TextView
        dueInfo = viewv!!.findViewById<View>(R.id.due_info) as TextView
        businessName = viewv!!.findViewById<View>(R.id.business_name) as? TextView
        creationDate = viewv!!.findViewById<View>(R.id.creation_date) as TextView
        clientName = viewv!!.findViewById<View>(R.id.client_name) as TextView
        invoiceDiscount = viewv!!.findViewById<View>(R.id.invoice_discount) as TextView
        shippingAmount = viewv!!.findViewById<View>(R.id.shipping_amount) as TextView
        taxAmount = viewv!!.findViewById<View>(R.id.tax_amount) as TextView
        totalAmount = viewv!!.findViewById<View>(R.id.total_amount) as TextView
        paidAmount = viewv!!.findViewById<View>(R.id.paid_amount) as TextView
        dueAmount = viewv!!.findViewById<View>(R.id.due_amount) as TextView
        signedDate = viewv!!.findViewById<View>(R.id.signed_date) as TextView
        invoiceNotes = viewv!!.findViewById<View>(R.id.invoice_notes) as EditText
        discountText = viewv!!.findViewById<View>(R.id.discount_text) as TextView
        subtotalAmount = viewv!!.findViewById<View>(R.id.subtotal_amount) as TextView
        taxText = viewv!!.findViewById<View>(R.id.tax_text) as TextView
        paymentOptionText = viewv!!.findViewById<View>(R.id.payment_option_text) as TextView
        paymentOption = viewv!!.findViewById<View>(R.id.payment_option) as TextView
        unitCostQuantity = viewv!!.findViewById<View>(R.id.unit_cost_quantity) as TextView
        totalCost = viewv!!.findViewById<View>(R.id.total_cost) as TextView
        currencySign = formatCurrency(mActivity!!, this.settingsDTO!!.currencyFormat)
        val textView = unitCostQuantity
        textView!!.text = "1 * " + currencySign + "0.00"
        val textView2 = totalCost
        textView2!!.text = currencySign + "0.00"
        if (catalogDTO!!.id > 0) {
            updateInvoiceInfo()
        } else {
            addInvoiceInfo()
        }
        if (MyConstants.CATALOG_TYPE == 1) {
            dueInfo!!.visibility = 8
        }
        viewv!!.findViewById<View>(R.id.discount_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.add_photo_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.invoice_info_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.preview_fab).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.client_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.add_item_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.shipping_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.tax_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.paid_layout).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.signed_date).setOnClickListener(this)
        viewv!!.findViewById<View>(R.id.payment_option_layout).setOnClickListener(this)
        val nestedScrollView2 =
            viewv!!.findViewById<View>(R.id.nested_scrollview) as NestedScrollView
        nestedScrollView = nestedScrollView2
        nestedScrollView2.isSmoothScrollingEnabled = true
        val recyclerView = viewv!!.findViewById<View>(R.id.invoice_items_rv) as RecyclerView
        invoiceItemsRv = recyclerView
        recyclerView.setHasFixedSize(true)
        invoiceItemsRv!!.isNestedScrollingEnabled = false
        invoiceItemsRv!!.layoutManager = LinearLayoutManager(mActivity, 1, false)
        val itemAssociatedAdapter2 = ItemAssociatedAdapter(
            mActivity!!,
            itemAssociatedDTOS!!,
            catalogDTO!!.discountType,
            catalogDTO!!.taxType
        )
        itemAssociatedAdapter = itemAssociatedAdapter2
        invoiceItemsRv!!.adapter = itemAssociatedAdapter2
        val recyclerView2 = viewv!!.findViewById<View>(R.id.item_images_rv) as RecyclerView
        itemImagesRv = recyclerView2
        recyclerView2.setHasFixedSize(true)
        itemImagesRv!!.isNestedScrollingEnabled = false
        itemImagesRv!!.layoutManager = LinearLayoutManager(mActivity, 1, false)
        val itemImageAdapter2 = ItemImageAdapter(mActivity!!, imageDTOS!!)
        itemImageAdapter = itemImageAdapter2
        itemImagesRv!!.adapter = itemImageAdapter2
        if (MyConstants.CATALOG_TYPE == 1) {
            viewv!!.findViewById<View>(R.id.paid_layout).visibility = 8
        }
        invoiceNotes!!.addTextChangedListener(object : TextWatcher {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.EditInvoiceFragment.AnonymousClass1 */
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun afterTextChanged(editable: Editable) {
                var id = catalogDTO!!.id
                if (id == 0L) {
                    id = createNewInvoice()
                }
                LoadDatabase.instance!!.updateInvoiceNotes(
                    id,
                    invoiceNotes!!.text.toString().trim { it <= ' ' })
            }
        })
    }

    private fun updateInvoiceInfo() {
        this.invoiceName!!.text = catalogDTO!!.catalogName
        if (catalogDTO!!.paidStatus != 2) {
            val terms = catalogDTO!!.terms
            if (terms == 0) {
                dueInfo!!.text = ""
                dueInfo!!.setTextColor(mActivity!!.resources.getColor(R.color.black))
            } else if (terms != 1) {
                val date = Date()
                val date2 = Date()
                date2.time = catalogDTO!!.dueDate!!.toLong()
                val ceil = Math.ceil((date2.time - date.time).toDouble() / 8.64E7)
                if (ceil > 0.0) {
                    val textView = dueInfo
                    textView!!.text = "Due in " + ceil.toInt() + " Days"
                    dueInfo!!.setTextColor(mActivity!!.resources.getColor(R.color.black))
                } else if (ceil != 0.0) {
                    dueInfo!!.text = "Overdue"
                    dueInfo!!.setTextColor(mActivity!!.resources.getColor(R.color.delete_color_bg))
                } else {
                    dueInfo!!.text = "Due on receipt"
                    dueInfo!!.setTextColor(mActivity!!.resources.getColor(R.color.black))
                }
            } else {
                dueInfo!!.setText(R.string.due_on_receipt)
                dueInfo!!.setTextColor(mActivity!!.resources.getColor(R.color.black))
            }
        }
        dueInfo!!.setText(R.string.paid)
        dueInfo!!.setTextColor(mActivity!!.resources.getColor(R.color.green))
        creationDate!!.text = formatDate(
            mActivity!!,
            catalogDTO!!.creationDate!!.toLong(),
            this.settingsDTO!!.dateFormat
        )
        if (catalogDTO!!.clientDTO.id > 0) {
            clientName!!.text = catalogDTO!!.clientDTO.clientName
            clientName!!.setTextColor(mActivity!!.resources.getColor(R.color.black))
            return
        }
        clientName!!.setTextColor(mActivity!!.resources.getColor(R.color.transparent_grey))
        clientName!!.text = "Client"
    }

    @SuppressLint("WrongConstant")
    private fun addInvoiceInfo() {
        this.invoiceName!!.text = MyConstants.invoiceName
        if (MyConstants.CATALOG_TYPE == 1) {
            dueInfo!!.visibility = 4
        } else {
            dueInfo!!.text = mActivity!!.resources.getString(R.string.due_on_receipt)
        }
        creationDate!!.text = formatDate(
            mActivity!!,
            Calendar.getInstance().timeInMillis,
            this.settingsDTO!!.dateFormat
        )
        clientName!!.text = "Client"
        signedDate!!.text = SecurityConstants.Signature
    }

    override fun onClick(view2: View) {
        when (view2.id) {
            R.id.add_item_layout -> {
                start(
                    mActivity!!,
                    null,
                    catalogDTO!!.id,
                    catalogDTO!!.discountType,
                    catalogDTO!!.taxType
                )
                return
            }
            R.id.add_photo_layout -> {
                AddPhotoActivity.start(mActivity!!, null, catalogDTO!!.id)
                return
            }
            R.id.client_layout -> if ((catalogDTO!!.clientDTO == null || catalogDTO!!.clientDTO.id <= 0) && LoadDatabase.instance!!.clientList.size > 0) {
                start(mActivity!!, catalogDTO!!.id, true)
                return
            } else {
                start(mActivity!!, catalogDTO!!.clientDTO, catalogDTO!!.id, true)
                return
            }
            R.id.discount_layout -> {
                start(
                    view2.context,
                    catalogDTO!!.id,
                    catalogDTO!!.discountType,
                    catalogDTO!!.discountAmount,
                    catalogDTO!!.subTotalAmount
                )
                return
            }
            R.id.invoice_info_layout -> {
                InvoiceInfoActivity.start(view2.context, catalogDTO)
                return
            }
            R.id.paid_layout -> {
                start(
                    view2.context,
                    catalogDTO!!.id,
                    catalogDTO!!.paidAmount,
                    catalogDTO!!.totalAmount
                )
                return
            }
            R.id.payment_option_layout -> {
                start(view2.context, businessDTO, catalogDTO!!.id)
                return
            }
            R.id.preview_fab -> {
                InvoicePreviewActivity.start(view2.context, catalogDTO)
                return
            }
            R.id.shipping_layout -> if (catalogDTO!!.clientDTO.id != 0L) {
                start(mActivity!!, catalogDTO!!.invoiceShippingDTO, catalogDTO!!.id)
                return
            } else {
                Toast.makeText(mActivity, getString(R.string.choose_client_first), Toast.LENGTH_SHORT).show()
                return
            }
            R.id.signed_date -> {
                start(
                    view2.context,
                    catalogDTO!!.id,
                    catalogDTO!!.signedDate,
                    catalogDTO!!.signedUrl
                )
                return
            }
            R.id.tax_layout -> {
                start(
                    view2.context,
                    catalogDTO!!.id,
                    catalogDTO!!.taxType,
                    catalogDTO!!.taxLabel,
                    catalogDTO!!.taxRate
                )
                return
            }
            else -> return
        }
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
    override fun onReceiveModelChange(str: String?, i: Int) {
        if (!MyConstants.createDuplicateEntry) {
            try {
                val gson = Gson()
                when (i) {
                    MyConstants.ACTION_INVOICE_ITEM_ADDED, MyConstants.ACTION_INVOICE_ITEM_UPDATED, MyConstants.ACTION_INVOICE_ITEM_DELETED -> {
                        itemAssociatedDTO =
                            gson.fromJson(str, ItemAssociatedDTO::class.java) as ItemAssociatedDTO
                        LoadDatabase.instance!!.calculateInvoiceBalance(catalogDTO!!.id)
                        updateInvoiceInfo()
                    }
                    MyConstants.ACTION_INVOICE_IMAGE_ADDED, MyConstants.ACTION_INVOICE_IMAGE_UPDATED, MyConstants.ACTION_INVOICE_IMAGE_DELETED -> imageDTO =
                        gson.fromJson(str, ImageDTO::class.java) as ImageDTO
                }
                when (i) {
                    101 -> {
                        val catalogDTO2 = gson.fromJson(str, CatalogDTO::class.java) as CatalogDTO
                        catalogDTO!!.subTotalAmount = catalogDTO2.subTotalAmount
                        catalogDTO!!.discountType = catalogDTO2.discountType
                        catalogDTO!!.discountAmount = catalogDTO2.discountAmount
                        catalogDTO!!.taxType = catalogDTO2.taxType
                        catalogDTO!!.taxLabel = catalogDTO2.taxLabel
                        catalogDTO!!.taxRate = catalogDTO2.taxRate
                        catalogDTO!!.taxAmount = catalogDTO2.taxAmount
                        catalogDTO!!.invoiceShippingDTO = catalogDTO2.invoiceShippingDTO
                        catalogDTO!!.totalAmount = catalogDTO2.totalAmount
                        catalogDTO!!.paidAmount = catalogDTO2.paidAmount
                        catalogDTO!!.paidStatus = catalogDTO2.paidStatus
                        catalogDTO!!.notes = catalogDTO2.notes
                        updateInvoiceBalance()
                        return
                    }
                    102 -> {
                        val discountDTO = gson.fromJson(str, DiscountDTO::class.java) as DiscountDTO
                        val itemAssociatedAdapter2 = itemAssociatedAdapter
                        if (itemAssociatedAdapter2 != null) {
                            itemAssociatedAdapter2.updateDiscount(discountDTO.discountType)
                            itemAssociatedAdapter!!.notifyDataSetChanged()
                        }
                        if (discountDTO.discountType == 2) {
                            catalogDTO!!.discount = discountDTO.discountAmount
                        }
                        LoadDatabase.instance!!.calculateInvoiceBalance(catalogDTO!!.id)
                        updateInvoiceInfo()
                        return
                    }
                    103 -> {
                        val taxDTO = gson.fromJson(str, TaxDTO::class.java) as TaxDTO
                        val itemAssociatedAdapter3 = itemAssociatedAdapter
                        itemAssociatedAdapter3?.updateTax(taxDTO.taxType)
                        LoadDatabase.instance!!.calculateInvoiceBalance(catalogDTO!!.id)
                        updateInvoiceInfo()
                        return
                    }
                    104 -> {
                        LoadDatabase.instance!!.calculateInvoiceBalance(catalogDTO!!.id)
                        updateInvoiceInfo()
                        return
                    }
                    105 -> {
                        val signedDTO = gson.fromJson(str, SignedDTO::class.java) as SignedDTO
                        catalogDTO!!.signedDate = signedDTO.signedDate
                        catalogDTO!!.signedUrl = signedDTO.signedUrl
                        if (TextUtils.isEmpty(catalogDTO!!.signedDate)) {
                            signedDate!!.text = SecurityConstants.Signature
                            return
                        }
                        val textView = signedDate
                        textView!!.text = "Signed " + formatDate(
                            mActivity!!,
                            catalogDTO!!.signedDate!!.toLong(),
                            this.settingsDTO!!.dateFormat
                        )
                        return
                    }
                    else -> {
                        if (i == 2001 || i == 2002) {
                            catalogDTO = gson.fromJson(str, CatalogDTO::class.java) as CatalogDTO
                            updateInvoiceInfo()
                            return
                        }
                        var i2 = 0
                        when (i) {
                            MyConstants.ACTION_INVOICE_ITEM_ADDED -> {
                                itemAssociatedDTOS!!.add(itemAssociatedDTO!!)
                                itemAssociatedAdapter!!.notifyDataSetChanged()
                                if (invoiceItemsRv!!.visibility != 0) {
                                    invoiceItemsRv!!.visibility = 0
                                    return
                                }
                                return
                            }
                            MyConstants.ACTION_INVOICE_ITEM_UPDATED -> {
                                while (i2 < itemAssociatedDTOS!!.size) {
                                    if (itemAssociatedDTOS!![i2]!!.id == itemAssociatedDTO!!.id) {
                                        val itemAssociatedDTO2 = itemAssociatedDTOS!![i2]
                                        itemAssociatedDTO2!!.itemName = itemAssociatedDTO!!.itemName
                                        itemAssociatedDTO2.description =
                                            itemAssociatedDTO!!.description
                                        itemAssociatedDTO2.unitCost = formatDecimal(
                                            itemAssociatedDTO2.unitCost

                                        )
                                        itemAssociatedDTO2.taxAble = itemAssociatedDTO!!.taxAble
                                        itemAssociatedDTO2.quantity = itemAssociatedDTO!!.quantity
                                        itemAssociatedDTO2.taxRate = itemAssociatedDTO!!.taxRate
                                        itemAssociatedDTO2.discount = itemAssociatedDTO!!.discount
                                        itemAssociatedDTO2.totalAmount = formatDecimal(
                                            itemAssociatedDTO2.totalAmount
                                        )
                                        itemAssociatedAdapter!!.notifyItemChanged(i2)
                                        return
                                    }
                                    i2++
                                }
                                return
                            }
                            MyConstants.ACTION_INVOICE_ITEM_DELETED -> {
                                while (i2 < itemAssociatedDTOS!!.size) {
                                    if (itemAssociatedDTOS!![i2]!!.id == itemAssociatedDTO!!.id) {
                                        itemAssociatedDTOS!!.removeAt(i2)
                                        itemAssociatedAdapter!!.notifyItemRemoved(i2)
                                        if (itemAssociatedDTOS!!.size == 0) {
                                            invoiceItemsRv!!.visibility = 8
                                            return
                                        }
                                    }
                                    i2++
                                }
                                return
                            }
                            MyConstants.ACTION_INVOICE_IMAGE_ADDED -> {
                                imageDTOS!!.add(imageDTO!!)
                                itemImageAdapter!!.notifyDataSetChanged()
                                if (itemImagesRv!!.visibility != 0) {
                                    itemImagesRv!!.visibility = 0
                                    return
                                }
                                return
                            }
                            MyConstants.ACTION_INVOICE_IMAGE_UPDATED -> {
                                while (i2 < imageDTOS!!.size) {
                                    if (imageDTOS!![i2]!!.id == imageDTO!!.id) {
                                        val imageDTO2 = imageDTOS!![i2]
                                        imageDTO2!!.imageUrl = imageDTO!!.imageUrl
                                        imageDTO2.description = imageDTO!!.description
                                        imageDTO2.additionalDetails = imageDTO!!.additionalDetails
                                        itemImageAdapter!!.notifyItemChanged(i2)
                                        return
                                    }
                                    i2++
                                }
                                return
                            }
                            MyConstants.ACTION_INVOICE_IMAGE_DELETED -> {
                                while (i2 < imageDTOS!!.size) {
                                    if (imageDTOS!![i2]!!.id == imageDTO!!.id) {
                                        imageDTOS!!.removeAt(i2)
                                        itemImageAdapter!!.notifyItemRemoved(i2)
                                        if (imageDTOS!!.size == 0) {
                                            itemImagesRv!!.visibility = 8
                                            return
                                        }
                                    }
                                    i2++
                                }
                                return
                            }
                            else -> when (i) {
                                MyConstants.ACTION_INVOICE_CLIENT_ADDED, MyConstants.ACTION_INVOICE_CLIENT_UPDATED -> {
                                    catalogDTO!!.clientDTO =
                                        (gson.fromJson(str, ClientDTO::class.java) as ClientDTO)
                                    clientName!!.text = catalogDTO!!.clientDTO.clientName
                                    clientName!!.setTextColor(mActivity!!.resources.getColor(R.color.black))
                                    return
                                }
                                MyConstants.ACTION_INVOICE_CLIENT_DELETED -> {
                                    catalogDTO!!.clientDTO.id = 0
                                    clientName!!.text = "No Client"
                                    clientName!!.setTextColor(mActivity!!.resources.getColor(R.color.transparent_grey))
                                    return
                                }
                                else -> when (i) {
                                    MyConstants.ACTION_INVOICE_SHIPPING_ADDED, MyConstants.ACTION_INVOICE_SHIPPING_UPDATED, MyConstants.ACTION_INVOICE_SHIPPING_DELETED -> {
                                        LoadDatabase.instance!!.calculateInvoiceBalance(catalogDTO!!.id)
                                        updateInvoiceInfo()
                                        return
                                    }
                                    else -> {
                                        if (i == 10001 || i == 10002) {
                                            val businessDTO2 = gson.fromJson(
                                                str,
                                                BusinessDTO::class.java
                                            ) as BusinessDTO
                                            businessDTO!!.paypalAddress = businessDTO2.paypalAddress
                                            businessDTO!!.checkInformation =
                                                businessDTO2.checkInformation
                                            businessDTO!!.bankInformation =
                                                businessDTO2.bankInformation
                                            businessDTO!!.otherPaymentInformation =
                                                businessDTO2.otherPaymentInformation
                                            val makeBusinessString = makeBusinessString()
                                            if (makeBusinessString.length <= 0) {
                                                paymentOption!!.text = ""
                                                paymentOption!!.visibility = 8
                                                return
                                            }
                                            val textView2 = paymentOptionText
                                            textView2!!.text =
                                                mActivity!!.resources.getString(R.string.payment_options) + ":"
                                            paymentOption!!.text = makeBusinessString
                                            paymentOption!!.visibility = 0
                                            return
                                        }
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateInvoiceBalance() {
        val textView = shippingAmount
        textView!!.text = currencySign + formatDecimal(

            catalogDTO!!.invoiceShippingDTO.amount
        )

        val textView2 = taxAmount
        textView2!!.text = currencySign + formatDecimal(

            catalogDTO!!.taxAmount
        )

        val textView3 = totalAmount
        textView3!!.text = currencySign + formatDecimal(

            catalogDTO!!.totalAmount.toDouble()
        )

        val textView4 = paidAmount
        textView4!!.text = currencySign + formatDecimal(

            catalogDTO!!.paidAmount.toDouble()
        )

        if (TextUtils.isEmpty(catalogDTO!!.signedDate)) {
            signedDate!!.text = SecurityConstants.Signature
        } else {
            val textView5 = signedDate
            textView5!!.text = "Signed " + formatDate(
                mActivity!!,
                catalogDTO!!.signedDate!!.toLong(),
                this.settingsDTO!!.dateFormat
            )
        }
        invoiceNotes!!.setText(catalogDTO!!.notes)
        val textView6 = subtotalAmount
        textView6!!.text = currencySign + formatDecimal(

            catalogDTO!!.subTotalAmount
        )

        if (catalogDTO!!.discountType == 2) {
            val textView7 = discountText
            textView7!!.text = "Discount (" + formatDecimal(

                catalogDTO!!.discount

            ) + "%)"
        } else {
            discountText!!.text = "Discount"
        }
        val textView8 = invoiceDiscount
        textView8!!.text = currencySign + formatDecimal(

            catalogDTO!!.discountAmount
        )

        if (catalogDTO!!.taxType == 3 || catalogDTO!!.taxType == 2) {
            taxText!!.text = "Tax"
        } else {
            val textView9 = taxText
            textView9!!.text = "Tax (" + formatDecimal(

                catalogDTO!!.taxRate
            )+ "%)"
        }
        val textView10 = taxAmount
        textView10!!.text = currencySign + formatDecimal(

            catalogDTO!!.taxAmount
        )

        val textView11 = paidAmount
        textView11!!.text = currencySign + formatDecimal(

            catalogDTO!!.paidAmount.toDouble()

        )
        val textView12 = dueAmount
        textView12!!.text =
            currencySign + formatDecimal((catalogDTO!!.totalAmount.toDouble() - catalogDTO!!.paidAmount.toDouble()))
    }

    // androidx.fragment.app.Fragment
    override fun onDestroy() {
        super.onDestroy()
        DataProcessor.instance!!.removeChangeListener(this)
    }
}