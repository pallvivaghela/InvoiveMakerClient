package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
import android.widget.TextView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.CatalogDTO
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import android.widget.Toast
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.PaymentDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.EditInvoiceFragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.InvoiceHistoryFragment
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.PagerAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity
import com.google.gson.Gson
import com.itextpdf.xmp.options.PropertyOptions
import java.lang.Exception
import java.util.*

public class InvoiceDetailsActivity : AppCompatActivity(), View.OnClickListener, ModelChangeListener {
    private var activityName: TextView? = null
    private var callerActivity: String? = null
    private var catalogDTO: CatalogDTO? = null
    private var pagerAdapter: PagerAdapter? = null
    private var tabLayout: TabLayout? = null
    private var toolbar: Toolbar? = null
    private var viewPager: ViewPager? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_invoice_details)
        DataProcessor.instance!!.addChangeListener(this)
        intentData
        initLayout()
        setUpTabLayout(bundle)
    }

    private val intentData: Unit
        private get() {
            catalogDTO = intent.getSerializableExtra(MyConstants.CATALOG_DTO) as CatalogDTO?
            callerActivity = intent.getStringExtra(MyConstants.CALLER_ACTIVITY)
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.invoice_details_menu, menu)
        if (MyConstants.CATALOG_TYPE == 1) {
            menu.findItem(R.id.mark_paid).isVisible = false
            menu.findItem(R.id.make_invoice).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.delete_invoice -> {
                if (checkValidity()) {
                    try {
                        val instance = LoadDatabase.instance
                        val catalogDTO2 = catalogDTO
                        instance!!.deleteInvoice(catalogDTO2?.id ?: 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    finish()
                }
                true
            }
            R.id.duplicate -> {
                if (checkValidity()) {
                    createDuplicateCatalog()
                }
                true
            }
            R.id.make_invoice -> {
                if (checkValidity()) {
                    UpdateEstimateData()
                }
                true
            }
            R.id.mark_paid -> {
                if (checkValidity()) {
                    UpdateInvoiceData()
                }
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    private fun createDuplicateCatalog() {
        MyConstants.DUPLICATE_ENTRY_FOR = catalogDTO!!.type
        if (MyConstants.CATALOG_TYPE == 1) {
            catalogDTO!!.estimateStatus = 1
        }
        var catalogDTO2: CatalogDTO? = null
        try {
            catalogDTO2 = catalogDTO!!.clone()
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        if (MyConstants.CATALOG_TYPE == 1) {
            MyConstants.invoiceCount = LoadDatabase.instance!!.allEstimates.size + 1
        } else {
            MyConstants.invoiceCount = LoadDatabase.instance!!.allInvoices.size + 1
        }
        catalogDTO2!!.catalogName = MyConstants.invoiceName
        try {
            catalogDTO2.id = LoadDatabase.instance!!.createDuplicate(catalogDTO2)
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
        finish()
        start(this, catalogDTO2)
    }

    @SuppressLint("WrongConstant")
    private fun checkValidity(): Boolean {
        if (catalogDTO != null) {
            return true
        }
        Toast.makeText(this, "Invoice/Estimate has not created yet!", 0).show()
        return false
    }

    private fun UpdateInvoiceData() {
        val totalAmount = catalogDTO!!.totalAmount - catalogDTO!!.paidAmount
        if (totalAmount != 0 && catalogDTO!!.id != 0L) {
            val paymentDTO = PaymentDTO()
            paymentDTO.catalogId = catalogDTO!!.id
            paymentDTO.paidAmount = totalAmount.toDouble()
            paymentDTO.paymentDate = Calendar.getInstance().timeInMillis.toString()
            paymentDTO.paymentMethod = "Others"
            LoadDatabase.instance!!.addPayment(paymentDTO)
        }
    }

    private fun UpdateEstimateData() {
        catalogDTO!!.estimateStatus = 2
        LoadDatabase.instance!!.updateInvoice(catalogDTO!!)
        MyConstants.createDuplicateEntry = true
        try {
            copyEstimateToInvoice()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MyConstants.createDuplicateEntry = false
    }

    private fun copyEstimateToInvoice() {
        val catalogDTO2: CatalogDTO?
        MyConstants.DUPLICATE_ENTRY_FOR = catalogDTO!!.type
        catalogDTO2 = try {
            catalogDTO!!.clone()
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            null
        }
        MyConstants.CATALOG_TYPE = 0
        MyConstants.invoiceCount = LoadDatabase.instance!!.allInvoices.size + 1
        catalogDTO2!!.catalogName = MyConstants.invoiceName
        try {
            catalogDTO2.id = LoadDatabase.instance!!.createDuplicate(catalogDTO2)
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
        finish()
        start(this, catalogDTO2)
    }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityName = findViewById<View>(R.id.activity_name) as TextView
        if (MyConstants.CATALOG_TYPE == 1) {
            activityName!!.text = "Estimate"
        } else {
            activityName!!.text = "Invoice"
        }
        findViewById<View>(R.id.send_text).setOnClickListener(this)
        findViewById<View>(R.id.send_email).setOnClickListener(this)
    }

    // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity
    public override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt("tabsCount", pagerAdapter!!.count)
        bundle.putStringArray("titles", pagerAdapter!!.titles.toTypedArray() as Array<String?>)
    }

    private fun setUpTabLayout(bundle: Bundle?) {
        viewPager = findViewById<View>(R.id.viewPager) as ViewPager
        pagerAdapter = PagerAdapter(
            supportFragmentManager, this
        )
        if (bundle == null) {
            val bundle2 = Bundle()
            bundle2.putSerializable(MyConstants.CATALOG_DTO, catalogDTO)
            val editInvoiceFragment = EditInvoiceFragment()
            editInvoiceFragment.arguments = bundle2
            pagerAdapter!!.addFragment(
                editInvoiceFragment,
                resources.getString(R.string.edit_invoice_text),
                null
            )
            val bundle3 = Bundle()
            val str = MyConstants.CATALOG_DTO
            val catalogDTO2 = catalogDTO
            bundle3.putString(str, catalogDTO2?.creationDate)
            val invoiceHistoryFragment = InvoiceHistoryFragment()
            invoiceHistoryFragment.arguments = bundle3
            pagerAdapter!!.addFragment(
                invoiceHistoryFragment,
                resources.getString(R.string.invoice_history_text),
                null
            )
        } else {
            val valueOf = Integer.valueOf(bundle.getInt("tabsCount"))
            val stringArray = bundle.getStringArray("titles")
            for (i in 0 until valueOf.toInt()) {
                pagerAdapter!!.addFragment(getFragment(i, bundle), stringArray!![i], null)
            }
        }
        viewPager!!.adapter = pagerAdapter
        viewPager!!.offscreenPageLimit = 3
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceDetailsActivity.AnonymousClass1 */

            override fun onPageScrollStateChanged(i: Int) {}


            override fun onPageScrolled(i: Int, f: Float, i2: Int) {}


            override fun onPageSelected(i: Int) {}
        })
        val tabLayout2 = findViewById<View>(R.id.tabLayout) as TabLayout
        tabLayout = tabLayout2
        tabLayout2.tabGravity = 0
        tabLayout!!.setupWithViewPager(viewPager)
    }

    private fun getFragment(i: Int, bundle: Bundle?): Fragment {
        return if (bundle == null) pagerAdapter!!.getItem(i) else supportFragmentManager.findFragmentByTag(
            getFragmentTag(i)
        )!!
    }

    private fun getFragmentTag(i: Int): String {
        return "android:switcher:2131296670:$i"
    }

    override fun onClick(view: View) {
        if (view.id == R.id.add_photo_layout) {
            startActivity(Intent(this, AddPhotoActivity::class.java))
        }
    }

    @SuppressLint("WrongConstant")
    private fun updateOnBackPressed() {
        val str = callerActivity
        if (str != null && str == javaClass.simpleName) {
            MyConstants.CATALOG_TYPE = MyConstants.DUPLICATE_ENTRY_FOR
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("from_app", true)
            intent.flags = 268468224
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        updateOnBackPressed()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
    override fun onReceiveModelChange(str: String?, i: Int) {
        if (!MyConstants.createDuplicateEntry) {
            val gson = Gson()
            if (i == 2001 || i == 2002) {
                val catalogDTO2 = gson.fromJson(str, CatalogDTO::class.java) as CatalogDTO
                if (catalogDTO == null) {
                    catalogDTO = CatalogDTO()
                }
                catalogDTO!!.catalogName = catalogDTO2.catalogName
                catalogDTO!!.creationDate = catalogDTO2.creationDate
                catalogDTO!!.clientDTO = catalogDTO2.clientDTO
                catalogDTO!!.dueDate = catalogDTO2.dueDate
                catalogDTO!!.discount = catalogDTO2.discount
                catalogDTO!!.discountType = catalogDTO2.discountType
                catalogDTO!!.discountAmount = catalogDTO2.discountAmount
                catalogDTO!!.estimateStatus = catalogDTO2.estimateStatus
                catalogDTO!!.id = catalogDTO2.id
                catalogDTO!!.invoiceShippingDTO = catalogDTO2.invoiceShippingDTO
                catalogDTO!!.notes = catalogDTO2.notes
                catalogDTO!!.paidAmount = catalogDTO2.paidAmount
                catalogDTO!!.paidStatus = catalogDTO2.paidStatus
                catalogDTO!!.poNumber = catalogDTO2.poNumber
                catalogDTO!!.subTotalAmount = catalogDTO2.subTotalAmount
                catalogDTO!!.signedUrl = catalogDTO2.signedUrl
                catalogDTO!!.signedDate = catalogDTO2.signedDate
                catalogDTO!!.taxType = catalogDTO2.taxType
                catalogDTO!!.taxLabel = catalogDTO2.taxLabel
                catalogDTO!!.taxRate = catalogDTO2.taxRate
                catalogDTO!!.taxAmount = catalogDTO2.taxAmount
                catalogDTO!!.totalAmount = catalogDTO2.totalAmount
                catalogDTO!!.type = catalogDTO2.type
                catalogDTO!!.terms = catalogDTO2.terms
                return
            }
            catalogDTO = LoadDatabase.instance!!.getSingleCatalog(catalogDTO!!.id)
        }
    }


    public override fun onDestroy() {
        super.onDestroy()
        DataProcessor.instance!!.removeChangeListener(this)
    }

    companion object {
        @SuppressLint("WrongConstant")
        @JvmStatic
        fun start(context: Context, catalogDTO2: CatalogDTO?) {
            val intent = Intent(context, InvoiceDetailsActivity::class.java)
            intent.putExtra(MyConstants.CALLER_ACTIVITY, context.javaClass.simpleName)
            intent.putExtra(MyConstants.CATALOG_DTO, catalogDTO2)
            intent.flags = PropertyOptions.DELETE_EXISTING
            context.startActivity(intent)
        }
    }

}