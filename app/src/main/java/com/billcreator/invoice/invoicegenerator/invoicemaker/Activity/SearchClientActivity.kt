package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddClientActivity.Companion.start
import androidx.appcompat.app.AppCompatActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ClientAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientDTO
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

class SearchClientActivity : AppCompatActivity(), View.OnClickListener {
    private var catalogId: Long = 0
    private var clientAdapter: ClientAdapter? = null
    private var clientDTOS: ArrayList<ClientDTO>? = null
    private var clientsRv: RecyclerView? = null
    private var fromCatalog = false
    private var toolbar: Toolbar? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_search_item)
        clientDTOS = ArrayList()
        intentData
        initLayout()
    }

    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            fromCatalog = intent.getBooleanExtra(MyConstants.FROM_CATALOG, false)
        }


    public override fun onResume() {
        super.onResume()
        clientDTOS!!.clear()
        clientDTOS!!.addAll(LoadDatabase.instance!!.clientList)
        clientAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setTitle(R.string.clients)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val recyclerView = findViewById<View>(R.id.my_items_rv) as RecyclerView
        clientsRv = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, 1, false)
        clientsRv!!.setHasFixedSize(true)
        val clientAdapter2 = ClientAdapter(this, clientDTOS!!, catalogId, fromCatalog)
        clientAdapter = clientAdapter2
        clientsRv!!.adapter = clientAdapter2
        findViewById<View>(R.id.add_client).setOnClickListener(this)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(view: View) {
        if (view.id == R.id.add_client) {
            finish()
            start(this, ClientDTO(), catalogId, true)
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, j: Long, z: Boolean) {
            val intent = Intent(context, SearchClientActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.FROM_CATALOG, z)
            context.startActivity(intent)
        }
    }
}