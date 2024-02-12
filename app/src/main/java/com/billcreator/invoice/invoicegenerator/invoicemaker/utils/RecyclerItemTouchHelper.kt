package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ItemAdapter

class RecyclerItemTouchHelper(
    i: Int,
    i2: Int,
    private val listener: RecyclerItemTouchHelperListener
) : ItemTouchHelper.SimpleCallback(i, i2) {
    interface RecyclerItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, i: Int, i2: Int)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder2: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, i: Int) {
        if (viewHolder != null) {
            getDefaultUIUtil().onSelected((viewHolder as ItemAdapter.ItemHolder).viewForeground)
        }
    }

    override fun onChildDrawOver(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        f: Float,
        f2: Float,
        i: Int,
        z: Boolean
    ) {
        getDefaultUIUtil().onDrawOver(
            canvas,
            recyclerView,
            (viewHolder as ItemAdapter.ItemHolder).viewForeground,
            f,
            f2,
            i,
            z
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        getDefaultUIUtil().clearView((viewHolder as ItemAdapter.ItemHolder).viewForeground)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        f: Float,
        f2: Float,
        i: Int,
        z: Boolean
    ) {
        getDefaultUIUtil().onDraw(
            canvas,
            recyclerView,
            (viewHolder as ItemAdapter.ItemHolder).viewForeground,
            f,
            f2,
            i,
            z
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        listener.onSwiped(viewHolder, i, viewHolder.adapterPosition)
    }

    override fun convertToAbsoluteDirection(i: Int, i2: Int): Int {
        return super.convertToAbsoluteDirection(i, i2)
    }
}