package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup

class SegmentedRadioGroup : RadioGroup {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {}

    public override fun onFinishInflate() {
        super.onFinishInflate()
        changeButtonsImages()
    }

    private fun changeButtonsImages() {
        val childCount = super.getChildCount()
        if (childCount > 1) {
            super.getChildAt(0)
            var i = 1
            while (i < childCount - 1) {
                super.getChildAt(i)
                i++
            }
            super.getChildAt(i)
            return
        }
        super.getChildAt(0)
    }
}