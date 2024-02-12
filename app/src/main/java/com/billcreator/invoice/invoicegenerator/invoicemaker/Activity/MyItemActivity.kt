package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemDTO
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import java.lang.Exception

class MyItemActivity : AppCompatActivity(), View.OnClickListener {
    private var deleteItem: ImageView? = null
    private var itemAdditionalDetails: EditText? = null
    private var itemDTO: ItemDTO? = null
    private var itemName: EditText? = null
    private var itemTaxable: Switch? = null
    private var itemUnitCost: EditText? = null
    private var toolbar: Toolbar? = null
    private var unitCost = 0.0
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_my_item)
        intentData
        initLayout()
    }

    private val intentData: Unit
        private get() {
            itemDTO = intent.getSerializableExtra(ITEM_DTO) as ItemDTO?
        }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        var z = true
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val imageView = findViewById<View>(R.id.delete_item) as ImageView
        deleteItem = imageView
        imageView.setOnClickListener(this)
        if (itemDTO != null) {
            supportActionBar!!.setTitle(R.string.edit_item)
            deleteItem!!.visibility = 0
        } else {
            supportActionBar!!.setTitle(R.string.new_item)
        }
        itemName = findViewById<View>(R.id.item_name) as EditText
        itemUnitCost = findViewById<View>(R.id.item_unit_cost) as EditText
        itemAdditionalDetails = findViewById<View>(R.id.item_additional_details) as EditText
        itemTaxable = findViewById<View>(R.id.item_taxable) as Switch
        val itemDTO2 = itemDTO
        if (itemDTO2 != null) {
            itemName!!.setText(itemDTO2.itemName)
            val editText = itemUnitCost
            editText!!.setText("" + itemDTO!!.unitCost)
            itemAdditionalDetails!!.setText(itemDTO!!.itemDescription)
            val r0 = itemTaxable
            if (itemDTO!!.texable != 1) {
                z = false
            }
            r0!!.isChecked = z
        }
        findViewById<View>(R.id.cancel_item).setOnClickListener(this)
        findViewById<View>(R.id.save_item).setOnClickListener(this)
    }

    private fun messageForDiscardChanges() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.discard_changes_message))
        builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.MyItemActivity.AnonymousClass1 */
            finish()
        }
        builder.setNegativeButton(R.string.no) /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.MyItemActivity.AnonymousClass2 */{ dialogInterface, i -> }
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

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.cancel_item) {
            finish()
        } else if (id == R.id.delete_item) {
            LoadDatabase.instance!!.deleteMyItem(itemDTO!!.id)
            finish()
        } else if (id == R.id.save_item) {
            saveItem()
        }
    }

    private fun saveItem() {
        val trim = itemName!!.text.toString().trim { it <= ' ' }
        if (trim.equals("", ignoreCase = true)) {
            itemName!!.error = getString(R.string.cannot_empty)
            return
        }
        try {
            unitCost = java.lang.String.valueOf(itemUnitCost!!.text.toString().trim { it <= ' ' })
                .toDouble()
        } catch (unused: Exception) {
            unitCost = 0.0
        }
        if (itemDTO == null) {
            itemDTO = ItemDTO()
        }
        itemDTO!!.itemName = trim
        itemDTO!!.unitCost = MyConstants.formatDecimal(unitCost)
        itemDTO!!.itemDescription = itemAdditionalDetails!!.text.toString().trim { it <= ' ' }
        itemDTO!!.texable = if (itemTaxable!!.isChecked) 1 else 0
        if (itemDTO!!.id > 0) {
            LoadDatabase.instance!!.updateMyItem(itemDTO!!)
        } else {
            LoadDatabase.instance!!.saveMyItem(itemDTO!!)
        }
        finish()
    }

    companion object {
        var ITEM_DTO = "item_dto"
        @JvmStatic
        fun start(context: Context, itemDTO2: ItemDTO?) {
            val intent = Intent(context, MyItemActivity::class.java)
            intent.putExtra(ITEM_DTO, itemDTO2)
            context.startActivity(intent)
        }
    }
}