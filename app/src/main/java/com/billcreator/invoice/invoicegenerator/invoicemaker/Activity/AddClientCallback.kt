package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientDTO

interface AddClientCallback {
    fun addClient(client: ClientDTO?)
}