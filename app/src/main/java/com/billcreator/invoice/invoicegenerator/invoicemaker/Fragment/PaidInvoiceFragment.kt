package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.InvoiceAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.CatalogDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener

class PaidInvoiceFragment : Fragment(), ModelChangeListener {
    private var catalogDTOS: ArrayList<CatalogDTO>? = null
    private var invoiceAdapter: InvoiceAdapter? = null
    private var invoiceMessage: TextView? = null
    private var invoices: ArrayList<CatalogDTO>? = null
    private var lottieAnimationView: LottieAnimationView? = null
    private var mActivity: Activity? = null
    private var mainInvoiceRv: RecyclerView? = null
    private var vview: View? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        if (vview == null) {
            vview = layoutInflater.inflate(R.layout.main_fragment_layout, viewGroup, false)
            DataProcessor.instance?.addChangeListener(this)
            catalogDTOS = ArrayList()
            initLayout()
        }
        return vview
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
    }

    override fun onResume() {
        super.onResume()
        updateInvoicesFromDatabase()
        loadData()
    }

    override fun setUserVisibleHint(z: Boolean) {
        if (z) {
            loadData()
        }
        super.setUserVisibleHint(z)
    }

    @SuppressLint("WrongConstant")
    private fun loadData() {
        try {
            catalogDTOS!!.clear()
            catalogDTOS!!.addAll(invoices!!)
            if (catalogDTOS!!.size == 0) {
                invoiceMessage!!.visibility = 0
                lottieAnimationView!!.visibility = 0
            } else {
                invoiceMessage!!.visibility = 8
                lottieAnimationView!!.visibility = 8
            }
            val invoiceAdapter2 = invoiceAdapter
            invoiceAdapter2?.notifyDataSetChanged()
        } catch (unused: Exception) {
        }
    }

    private fun updateInvoicesFromDatabase() {
        invoices = instance!!.getUnPaidInvoices(2)
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val recyclerView = vview!!.findViewById<View>(R.id.main_invoice_rv) as RecyclerView
        mainInvoiceRv = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(mActivity, 1, false)
        mainInvoiceRv!!.setHasFixedSize(true)
        val invoiceAdapter2 = InvoiceAdapter(mActivity!!, catalogDTOS!!)
        invoiceAdapter = invoiceAdapter2
        mainInvoiceRv!!.adapter = invoiceAdapter2
        invoiceMessage = vview!!.findViewById<View>(R.id.invoice_message) as TextView
        lottieAnimationView = vview!!.findViewById<View>(R.id.lt_no_data) as LottieAnimationView
        invoiceMessage!!.text = mActivity!!.resources.getString(R.string.paid_invoice_message)
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.utils.ModelChangeListener
    override fun onReceiveModelChange(str: String?, i: Int) {
        if (i == 2001) {
            loadData()
            invoiceAdapter!!.filter(str.toString(), catalogDTOS!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DataProcessor.instance?.removeChangeListener(this)
    }
}