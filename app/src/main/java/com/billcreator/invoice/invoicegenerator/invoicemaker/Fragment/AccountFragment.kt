package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.R

class AccountFragment : Fragment() {
    private var mActivity: Activity? = null
    private var vview: View? = null
    private fun initLayout() {}


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
            vview = layoutInflater.inflate(R.layout.acccount_fragment_layout, viewGroup, false)
            initLayout()
        }
        return vview
    }
}