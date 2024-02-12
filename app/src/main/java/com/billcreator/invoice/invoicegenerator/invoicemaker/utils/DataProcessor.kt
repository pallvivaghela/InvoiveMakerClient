package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import java.lang.Exception
import java.util.ArrayList

class DataProcessor private constructor() {
    private val listeners = ArrayList<ModelChangeListener>()
    fun notifyListeners(str: String?, i: Int) {
        val it: Iterator<ModelChangeListener> = listeners.iterator()
        while (it.hasNext()) {
            it.next().onReceiveModelChange(str, i)
        }
    }

    fun addChangeListener(modelChangeListener: ModelChangeListener) {
        listeners.add(modelChangeListener)
    }

    fun removeChangeListener(modelChangeListener: ModelChangeListener) {
        try {
            listeners.remove(modelChangeListener)
        } catch (unused: Exception) {
        }
    }

    companion object {
        private var dataProcessor: DataProcessor? = null
        @JvmStatic
        val instance: DataProcessor?
            get() {
                if (dataProcessor == null) {
                    dataProcessor = DataProcessor()
                }
                return dataProcessor
            }
    }
}