package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.app.Activity
import androidx.fragment.app.FragmentStatePagerAdapter
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.ArrayList

class PagerAdapter(fragmentManager: FragmentManager?, private val mActivity: Activity) :
    FragmentStatePagerAdapter(
        fragmentManager!!
    ) {
    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitleIcon = ArrayList<Drawable?>()
    val titles = ArrayList<String>()


    override fun getItem(i: Int): Fragment {
        return fragmentList[i]
    }

    fun addFragment(fragment: Fragment, str: String, drawable: Drawable?) {
        fragmentList.add(fragment)
        titles.add(str)
        fragmentTitleIcon.add(drawable)
    }

    override fun getCount(): Int {
        return fragmentList.size
    }


    override fun getPageTitle(i: Int): CharSequence? {
        return SpannableStringBuilder(titles[i])
    }
}