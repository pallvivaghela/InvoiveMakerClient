package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

interface ModelChangeListener {
    fun onReceiveModelChange(str: String?, i: Int)
}