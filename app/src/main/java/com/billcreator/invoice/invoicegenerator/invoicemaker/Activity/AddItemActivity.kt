package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemAssociatedDTO
import androidx.appcompat.widget.SwitchCompat
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemDTO
import android.text.Editable
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import java.lang.Exception

class AddItemActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {
    var additionalDetails: EditText? = null
    var amount: TextView? = null
    var catalogId: Long = 0
    var cost = 0.0
    var currencySign: String? = null
    var deleteItem: ImageView? = null
    var disc = 0.0
    var discount: EditText? = null
    var discountLayout: TextInputLayout? = null
    var discountType = 0
    var itemAssociatedDTO: ItemAssociatedDTO? = null
    var itemName: EditText? = null
    var quant = 0.0
    var quantity: EditText? = null
    var rate = 0.0
    var saveItem: TextView? = null
    var saveItemForFuture: SwitchCompat? = null
    var searchItem: ImageView? = null
    var taxRate: EditText? = null
    var taxRateLayout: TextInputLayout? = null
    var taxType = 0
    var taxable = 1
    var taxableCheckbox: CheckBox? = null
    var taxableLayout: LinearLayout? = null
    var toolbar: Toolbar? = null
    var unitCost: EditText? = null
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_add_item)
        intentData
        initLayout()
    }

    private val intentData: Unit
        private get() {
            itemAssociatedDTO =
                intent.getSerializableExtra(MyConstants.ITEM_ASSOCIATED_DTO) as ItemAssociatedDTO?
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            discountType = intent.getIntExtra(MyConstants.DISCOUNT_TYPE, 0)
            taxType = intent.getIntExtra(MyConstants.TAX_TYPE, 0)
        }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        searchItem = findViewById<View>(R.id.search_item) as ImageView
        deleteItem = findViewById<View>(R.id.delete_item) as ImageView
        saveItem = findViewById<View>(R.id.save_item) as TextView
        searchItem!!.setOnClickListener(this)
        deleteItem!!.setOnClickListener(this)
        saveItem!!.setOnClickListener(this)
        itemName = findViewById<View>(R.id.item_name) as EditText
        val editText = findViewById<View>(R.id.unit_cost) as EditText
        unitCost = editText
        editText.addTextChangedListener(this)
        val editText2 = findViewById<View>(R.id.quantity) as EditText
        quantity = editText2
        editText2.addTextChangedListener(this)
        val editText3 = findViewById<View>(R.id.discount) as EditText
        discount = editText3
        editText3.addTextChangedListener(this)
        amount = findViewById<View>(R.id.amount) as TextView
        taxableCheckbox = findViewById<View>(R.id.taxable_checkbox) as CheckBox
        val editText4 = findViewById<View>(R.id.tax_rate) as EditText
        taxRate = editText4
        editText4.addTextChangedListener(this)
        taxRateLayout = findViewById<View>(R.id.tax_rate_layout) as TextInputLayout
        additionalDetails = findViewById<View>(R.id.additional_details) as EditText
        saveItemForFuture = findViewById<View>(R.id.save_item_for_future) as SwitchCompat
        discountLayout = findViewById<View>(R.id.discount_layout) as TextInputLayout
        taxableLayout = findViewById<View>(R.id.taxable_layout) as LinearLayout
        currencySign = SettingsDTO.settingsDTO?.let { MyConstants.formatCurrency(this, it.currencyFormat) }
        val textView = amount
        textView!!.text = currencySign + "0.00"
        taxableCheckbox!!.isChecked = true
        taxableCheckbox!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, z ->

            if (z) {
                if (taxType == 2) {
                    taxRateLayout!!.visibility = 0
                } else {
                    taxRateLayout!!.visibility = 8
                }
                taxable = 1
                return@OnCheckedChangeListener
            }
            taxRateLayout!!.visibility = 8
            taxable = 0
        })
        val itemAssociatedDTO2 = itemAssociatedDTO
        if (itemAssociatedDTO2 == null || itemAssociatedDTO2.id <= 0) {
            searchItem!!.visibility = 0
            supportActionBar!!.title = resources.getString(R.string.add_item)
        } else {
            deleteItem!!.visibility = 0
            supportActionBar!!.title = resources.getString(R.string.edit_item)
        }
        if (itemAssociatedDTO != null) {
            updateItemAsssociatedInfo()
        }
        if (discountType == 1) {
            discountLayout!!.visibility = 0
        } else {
            discountLayout!!.visibility = 8
        }
        if (taxType == 3) {
            taxableLayout!!.visibility = 8
        } else {
            taxableLayout!!.visibility = 0
        }
    }

    @SuppressLint("WrongConstant")
    private fun updateItemAsssociatedInfo() {
        itemName!!.setText(itemAssociatedDTO!!.itemName)
        val editText = unitCost
        editText!!.setText(itemAssociatedDTO!!.unitCost.toString() + "")
        val editText2 = quantity
        editText2!!.setText(itemAssociatedDTO!!.quantity.toString() + "")
        additionalDetails!!.setText(itemAssociatedDTO!!.description)
        val editText3 = discount
        editText3!!.setText(itemAssociatedDTO!!.discount.toString() + "")
        val editText4 = taxRate
        editText4!!.setText(itemAssociatedDTO!!.taxRate.toString() + "")
        val taxAble = itemAssociatedDTO!!.taxAble
        taxable = taxAble
        if (taxAble == 1) {
            if (taxType == 2) {
                taxRateLayout!!.visibility = 0
            } else {
                taxRateLayout!!.visibility = 8
            }
            taxableCheckbox!!.isChecked = true
            return
        }
        taxRateLayout!!.visibility = 8
        taxableCheckbox!!.isChecked = false
    }

    private fun updateTotalAmount() {
        try {
            cost = java.lang.String.valueOf(unitCost!!.text.toString()).toDouble()
        } catch (unused: Exception) {
            cost = 0.0
        }
        try {
            quant = java.lang.String.valueOf(quantity!!.text.toString()).toDouble()
        } catch (unused2: Exception) {
            quant = 0.0
        }
        try {
            rate = java.lang.String.valueOf(taxRate!!.text.toString()).toDouble()
        } catch (unused3: Exception) {
            rate = 0.0
        }
        try {
            disc = java.lang.String.valueOf(discount!!.text.toString()).toDouble()
        } catch (unused4: Exception) {
            disc = 0.0
        }
        val d = cost * quant
        val d2 = d - disc / 100.0 * d
        val textView = amount
        textView!!.text = currencySign + MyConstants.formatDecimal(d2)
    }

    private fun saveItem() {
        val obj = itemName!!.text.toString()
        itemName!!.requestFocus()
        if (obj.trim { it <= ' ' }.equals("", ignoreCase = true)) {
            itemName!!.error = "Cannot be empty"
            return
        }
        if (itemAssociatedDTO == null) {
            itemAssociatedDTO = ItemAssociatedDTO()
        }
        updateTotalAmount()
        itemAssociatedDTO!!.itemName = itemName!!.text.toString().trim { it <= ' ' }
        itemAssociatedDTO!!.description =
            additionalDetails!!.text.toString().trim { it <= ' ' }
        itemAssociatedDTO!!.unitCost = MyConstants.formatDecimal(java.lang.Double.valueOf(cost))
        itemAssociatedDTO!!.quantity = quant
        itemAssociatedDTO!!.taxAble = taxable
        itemAssociatedDTO!!.taxRate = rate
        itemAssociatedDTO!!.discount = disc
        if (catalogId == 0L) {
            catalogId = MyConstants.createNewInvoice()
        }
        itemAssociatedDTO!!.catalogId = catalogId
        if (itemAssociatedDTO!!.id > 0) {
            LoadDatabase.instance?.updateInvoiceItem(itemAssociatedDTO!!)
        } else {
            LoadDatabase.instance?.saveInvoiceItem(itemAssociatedDTO!!)
        }
        if (saveItemForFuture!!.isChecked) {
            LoadDatabase.instance?.saveMyItem(
                ItemDTO(
                    itemAssociatedDTO!!.itemName,
                    itemAssociatedDTO!!.description,
                    itemAssociatedDTO!!.unitCost,
                    itemAssociatedDTO!!.taxAble
                )
            )
        }
        finish()
    }

    private fun messageForDiscardChanges() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.discard_changes_message))
        builder.setPositiveButton("YES") { dialogInterface, i -> finish() }
        builder.setNegativeButton("NO") { dialogInterface, i -> }
        builder.create()
        builder.show()
    }


    override fun onBackPressed() {
        messageForDiscardChanges()
    }


    override fun onSupportNavigateUp(): Boolean {
        messageForDiscardChanges()
        return true
    }

    override fun afterTextChanged(editable: Editable) {
        updateTotalAmount()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.delete_item) {
            LoadDatabase.instance?.deleteInvoiceItem(itemAssociatedDTO!!.id)
            finish()
        } else if (id == R.id.save_item) {
            saveItem()
        } else if (id == R.id.search_item) {
            SearchItemActivity.start(view.context, catalogId)
        }
    }

    companion object {
        const val TAG = "AddItemActivity"
        @JvmStatic
        fun start(
            context: Context,
            itemAssociatedDTO2: ItemAssociatedDTO?,
            j: Long,
            i: Int,
            i2: Int
        ) {
            val intent = Intent(context, AddItemActivity::class.java)
            intent.putExtra(MyConstants.ITEM_ASSOCIATED_DTO, itemAssociatedDTO2)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.DISCOUNT_TYPE, i)
            intent.putExtra(MyConstants.TAX_TYPE, i2)
            context.startActivity(intent)
        }
    }
}