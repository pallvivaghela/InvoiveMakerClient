package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class NonScrollListView : ListView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {}
    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
    }

    public override fun onMeasure(i: Int, i2: Int) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(536870911, Int.MIN_VALUE))
        layoutParams.height = measuredHeight
    }
}