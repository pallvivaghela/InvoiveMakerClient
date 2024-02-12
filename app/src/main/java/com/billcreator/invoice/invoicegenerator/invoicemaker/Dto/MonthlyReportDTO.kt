package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

import java.util.HashSet

class MonthlyReportDTO {
    private var clientList: MutableSet<Long?>? = null
    private var totalClientsPerYear = 0
    var totalInvoices = 0
    var totalPaidAmount = 0.0
    var yearOrMonth = 0
    fun getTotalClientsPerYear(): Long {
        return totalClientsPerYear.toLong()
    }

    fun setTotalClientsPerYear(i: Int) {
        totalClientsPerYear = i
    }

    val totalClients: Long
        get() {
            val set = clientList ?: return 0
            return set.size.toLong()
        }

    fun setClients(j: Long) {
        if (clientList == null) {
            clientList = HashSet()
        }
        clientList!!.add(java.lang.Long.valueOf(j))
    }
}