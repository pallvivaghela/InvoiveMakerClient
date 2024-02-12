package com.billcreator.invoice.invoicegenerator.invoicemaker.Dto

class SettingsDTO {
    var currencyFormat = 0
    var dateFormat = 0
    var id: Long = 0

    companion object {
        @JvmStatic
        var settingsDTO: SettingsDTO? = null
    }
}