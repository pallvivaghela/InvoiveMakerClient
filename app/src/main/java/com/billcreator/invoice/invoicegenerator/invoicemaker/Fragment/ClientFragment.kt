package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddClientActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ClientAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ClientFragment : Fragment(), View.OnClickListener {
    private var addItem: FloatingActionButton? = null
    private var clientAdapter: ClientAdapter? = null
    private var clientDTOS: ArrayList<ClientDTO>? = null
    private var clients: ArrayList<ClientDTO>? = null
    private var clientsRv: RecyclerView? = null
    private var lottieAnimationView: LottieAnimationView? = null
    private var mActivity: Activity? = null
    private var noClientMessage: TextView? = null
    private var searchView: EditText? = null
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
            vview = layoutInflater.inflate(R.layout.client_fragment_layout, viewGroup, false)
            clientDTOS = ArrayList()
            initLayout()
        }
        return vview
    }

    override fun onResume() {
        super.onResume()
        updateInvoicesFromDatabase()
        loadData()
    }

    private fun loadData() {
        clientDTOS!!.clear()
        clientDTOS!!.addAll(clients!!)
        val clientAdapter2 = clientAdapter
        clientAdapter2?.notifyDataSetChanged()
        if (clientDTOS!!.size == 0) {
            noClientMessage!!.visibility = 0
            lottieAnimationView!!.visibility = 0
        }
    }

    private fun updateInvoicesFromDatabase() {
        clients = instance!!.clientList
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    private fun initLayout() {
        val floatingActionButton = vview!!.findViewById<View>(R.id.add_item) as FloatingActionButton
        addItem = floatingActionButton
        floatingActionButton.setOnClickListener(this)
        noClientMessage = vview!!.findViewById<View>(R.id.no_client_message) as TextView
        lottieAnimationView = vview!!.findViewById<View>(R.id.lt_no_data) as LottieAnimationView
        val recyclerView = vview!!.findViewById<View>(R.id.clients_rv) as RecyclerView
        clientsRv = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(mActivity, 1, false)
        clientsRv!!.setHasFixedSize(true)
        val clientAdapter2 = clientDTOS?.let { ClientAdapter(mActivity!!, it, 0, false) }
        clientAdapter = clientAdapter2
        clientsRv!!.adapter = clientAdapter2
        val editText = vview!!.findViewById<View>(R.id.search_view) as EditText
        searchView = editText
        editText.addTextChangedListener(object : TextWatcher {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.ClientFragment.AnonymousClass1 */
            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                loadData()
                clientAdapter!!.filter(charSequence.toString(), clientDTOS!!)
            }
        })
    }

    override fun onClick(view2: View) {
        if (view2.id == R.id.add_item) {
            start(view2.context, ClientDTO(), 0, false)
        }
    }
}