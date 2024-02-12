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
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoiceDetailsActivity.Companion.start
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.PagerAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.DataProcessor
import com.google.android.material.tabs.TabLayout

class EstimateFragment : Fragment(), View.OnClickListener {
    private var mActivity: Activity? = null
    private var pagerAdapter: PagerAdapter? = null
    private var searchView: EditText? = null
    private var tabLayout: TabLayout? = null
    private var vview: View? = null
    private var viewPager: ViewPager? = null
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
            val inflate = layoutInflater.inflate(R.layout.invoice_fragment_layout, viewGroup, false)
            vview = inflate
            val editText = inflate.findViewById<View>(R.id.searchView1) as EditText
            searchView = editText
            editText.addTextChangedListener(object : TextWatcher {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.EstimateFragment.AnonymousClass1 */
                override fun afterTextChanged(editable: Editable) {}
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i2: Int,
                    i3: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                    DataProcessor.instance?.notifyListeners(charSequence.toString(), 2001)
                }
            })
            initLayout()
            setUpTabLayout(bundle)
        }
        return vview
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt("tabsCount", pagerAdapter!!.count)
        bundle.putStringArray("titles", pagerAdapter!!.titles.toTypedArray<String?>())
    }

    private fun setUpTabLayout(bundle: Bundle?) {
        vview!!.findViewById<View>(R.id.add_item).setOnClickListener(this)
        viewPager = vview!!.findViewById<View>(R.id.viewPager) as ViewPager
        val pagerAdapter2 = PagerAdapter(
            childFragmentManager, mActivity!!
        )
        pagerAdapter = pagerAdapter2
        if (bundle == null) {
            pagerAdapter2.addFragment(
                AlIEstimateFragment(),
                resources.getString(R.string.all_text),
                resources.getDrawable(R.drawable.ic_estimates)
            )
            pagerAdapter!!.addFragment(
                OpenEstimateFragment(),
                resources.getString(R.string.open_text),
                resources.getDrawable(R.drawable.ic_estimates)
            )
            pagerAdapter!!.addFragment(
                ClosedEstimateFragment(),
                resources.getString(R.string.closed_text),
                resources.getDrawable(R.drawable.ic_estimates)
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

            override fun onPageScrollStateChanged(i: Int) {}

            override fun onPageScrolled(i: Int, f: Float, i2: Int) {}

            override fun onPageSelected(i: Int) {}
        })
        val tabLayout2 = vview!!.findViewById<View>(R.id.tabLayout) as TabLayout
        tabLayout = tabLayout2
        tabLayout2.tabGravity = 0
        tabLayout!!.setupWithViewPager(viewPager)
    }

    private fun getFragment(i: Int, bundle: Bundle?): Fragment {
        return if (bundle == null) pagerAdapter!!.getItem(i) else childFragmentManager.findFragmentByTag(
            getFragmentTag(i)
        )!!
    }

    private fun getFragmentTag(i: Int): String {
        return "android:switcher:2131296670:$i"
    }

    override fun onClick(view2: View) {
        if (view2.id == R.id.add_item) {
            start(view2.context, null)
        }
    }
}