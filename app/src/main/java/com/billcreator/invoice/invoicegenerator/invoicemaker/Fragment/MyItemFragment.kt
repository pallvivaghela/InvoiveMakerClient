package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.annotation.SuppressLint
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
import androidx.core.view.InputDeviceCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.MyItemActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ItemAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ItemDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.RecyclerItemTouchHelper
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MyItemFragment : Fragment(), View.OnClickListener, RecyclerItemTouchHelperListener {
    private var addItem: FloatingActionButton? = null
    private var deleteItemFlag = false
    private var itemAdapter: ItemAdapter? = null
    private var itemDTOS: ArrayList<ItemDTO>? = null
    private var items: ArrayList<ItemDTO>? = null
    private var lottieAnimationView: LottieAnimationView? = null
    private var mActivity: Activity? = null
    private var myItemSearchview1: EditText? = null
    private var myItemsRv: RecyclerView? = null
    private var noItemMessage: TextView? = null
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
            vview = layoutInflater.inflate(R.layout.my_item_fragment_layout, viewGroup, false)
            itemDTOS = ArrayList()
            initLayout()
        }
        return vview
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        updateItemsFromDatabase()
        loadData()
    }

    @SuppressLint("WrongConstant")
    private fun loadData() {
        itemDTOS!!.clear()
        val arrayList = items
        if (arrayList != null) {
            itemDTOS!!.addAll(arrayList)
        }
        val itemAdapter2 = itemAdapter
        itemAdapter2?.notifyDataSetChanged()
        if (itemDTOS!!.size == 0) {
            noItemMessage!!.visibility = 0
            lottieAnimationView!!.visibility = 0
            return
        }
        noItemMessage!!.visibility = 8
        lottieAnimationView!!.visibility = 8
    }

    private fun updateItemsFromDatabase() {
        items = instance!!.myItems
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        val floatingActionButton = vview!!.findViewById<View>(R.id.add_item) as FloatingActionButton
        addItem = floatingActionButton
        floatingActionButton.setOnClickListener(this)
        noItemMessage = vview!!.findViewById<View>(R.id.no_item_message) as TextView
        lottieAnimationView = vview!!.findViewById<View>(R.id.lt_no_data) as LottieAnimationView
        val recyclerView = vview!!.findViewById<View>(R.id.my_items_rv) as RecyclerView
        myItemsRv = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(mActivity, 1, false)
        myItemsRv!!.setHasFixedSize(true)
        val itemAdapter2 = itemDTOS?.let { ItemAdapter(mActivity!!, it, 0, false) }
        itemAdapter = itemAdapter2
        myItemsRv!!.adapter = itemAdapter2
        ItemTouchHelper(RecyclerItemTouchHelper(0, 4, this)).attachToRecyclerView(myItemsRv)
        val editText = vview!!.findViewById<View>(R.id.my_item_searchview1) as EditText
        myItemSearchview1 = editText
        editText.addTextChangedListener(object : TextWatcher {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.MyItemFragment.AnonymousClass1 */
            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                loadData()
                itemAdapter!!.filter(charSequence, itemDTOS!!)
            }
        })
    }

    override fun onClick(view2: View) {
        if (view2.id == R.id.add_item) {
            start(view2.context, null)
        }
    }

    // com.billcreator.invoice.invoicegenerator.invoicemaker.utils.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, i: Int, i2: Int) {
        if (viewHolder is ItemAdapter.ItemHolder) {
            val itemName = itemDTOS!![viewHolder.getAdapterPosition()]!!.itemName
            val itemDTO = itemDTOS!![viewHolder.getAdapterPosition()]
            val adapterPosition = viewHolder.getAdapterPosition()
            itemAdapter!!.removeItem(viewHolder.getAdapterPosition())
            items!!.remove(itemDTO)
            deleteItemFlag = true
            val view2 = getView()
            val make = Snackbar.make(view2!!, "$itemName removed from my items!", 0)
            make.setAction("UNDO") {
                itemAdapter!!.restoreItem(itemDTO!!, adapterPosition)
                items!!.add(adapterPosition, itemDTO)
                deleteItemFlag = false
            }
            make.addCallback(object : Snackbar.Callback() {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.MyItemFragment.AnonymousClass3 */
                // com.google.android.material.snackbar.Snackbar.Callback
                override fun onShown(snackbar: Snackbar) {}

                // com.google.android.material.snackbar.Snackbar.Callback
                override fun onDismissed(snackbar: Snackbar, i: Int) {
                    if (deleteItemFlag) {
                        instance!!.deleteMyItem(itemDTO!!.id)
                        updateItemsFromDatabase()
                    }
                }
            })
            make.setActionTextColor(InputDeviceCompat.SOURCE_ANY)
            make.show()
        }
    }

}