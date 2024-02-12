package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ItemAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemDTO
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.Toolbar
import java.util.ArrayList

class SearchItemActivity : AppCompatActivity() {
    private var catalogId: Long = 0
    private var itemAdapter: ItemAdapter? = null
    private var itemDTOS: ArrayList<ItemDTO>? = null
    private var myItemsRv: RecyclerView? = null
    private var toolbar: Toolbar? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_search_item)
        itemDTOS = ArrayList()
        intentData
        initLayout()
    }

    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
        }

    public override fun onResume() {
        super.onResume()
        itemDTOS!!.clear()
        itemDTOS!!.addAll(LoadDatabase.instance!!.myItems)
        itemAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.title = "Select Item"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val recyclerView = findViewById<View>(R.id.my_items_rv) as RecyclerView
        myItemsRv = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, 1, false)
        myItemsRv!!.setHasFixedSize(true)
        val itemAdapter2 = ItemAdapter(this, itemDTOS!!, catalogId, true)
        itemAdapter = itemAdapter2
        myItemsRv!!.adapter = itemAdapter2
        findViewById<View>(R.id.add_client).visibility = 8
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun start(context: Context, j: Long) {
            val intent = Intent(context, SearchItemActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            context.startActivity(intent)
        }
    }
}