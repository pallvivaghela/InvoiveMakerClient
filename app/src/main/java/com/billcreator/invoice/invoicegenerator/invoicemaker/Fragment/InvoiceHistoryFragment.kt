package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.AddPhotoActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import java.text.SimpleDateFormat
import java.util.*

class InvoiceHistoryFragment : Fragment(), View.OnClickListener {
    private var creationDate: TextView? = null
    private var date: String? = null
    private var mActivity: Activity? = null
    private var settingsDTO: SettingsDTO? = null
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
            vview = layoutInflater.inflate(R.layout.invoice_history_layout, viewGroup, false)
            intentData
            initLayout()
        }
        return vview
    }

    private val intentData: Unit
        @SuppressLint("UseRequireInsteadOfGet") private get() {
            date = arguments!!.getString(MyConstants.CATALOG_DTO)
            this.settingsDTO = SettingsDTO.settingsDTO
        }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        creationDate = vview!!.findViewById<View>(R.id.creation_date) as TextView
        val str = date
        if (str != null) {
            val parseLong = str.toLong()
            val date2 = Date(parseLong)
            val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")
            val textView = creationDate
            textView!!.text = mActivity?.let {
                MyConstants.formatDate(
                    it,
                    parseLong,
                    this.settingsDTO!!.dateFormat
                )
            } + " " + simpleDateFormat.format(date2)
            return
        }
        vview!!.findViewById<View>(R.id.history_layout).visibility = 8
    }

    override fun onClick(view2: View) {
        if (view2.id == R.id.add_photo_layout) {
            startActivity(Intent(mActivity, AddPhotoActivity::class.java))
        }
    }
}