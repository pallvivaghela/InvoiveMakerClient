package com.billcreator.invoice.invoicegenerator.invoicemaker.Database

abstract class DatabaseOpenClose {
    abstract fun closeDB()
    abstract fun openDb()
}