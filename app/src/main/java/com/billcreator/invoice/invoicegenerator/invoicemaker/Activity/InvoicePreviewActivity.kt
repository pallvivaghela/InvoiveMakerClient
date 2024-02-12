package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.MyPrintDocumentAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.Contract
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.*
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.formatCurrency
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.formatDate
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.formatDecimal
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.invoiceName
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants.rootDirectory
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.shockwave.pdfium.PdfDocument.Bookmark
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class InvoicePreviewActivity : AppCompatActivity(), OnPageChangeListener,
    OnLoadCompleteListener {
    private var businessDTO: BusinessDTO? = null
    private var catalogDTO: CatalogDTO? = null
    private var currencySign: String? = null
    private val imageDTOS = ArrayList<ImageDTO>()
    private var itemAssociatedDTOS: ArrayList<ItemAssociatedDTO>? = null
    var normalWhite = Font(Font.FontFamily.HELVETICA, 15.0f, 0, BaseColor.WHITE)
    var pageNumber = 0
    var pdfFileName: String? = null
    var pdfView: PDFView? = null
    private var settingsDTO: SettingsDTO? = null
    var textBaseFont: BaseFont? = null
    var textFont = Font(Font.FontFamily.TIMES_ROMAN, 14.0f)
    var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 18.0f, 1, BaseColor.BLACK)
    private var toolbar: Toolbar? = null
    private val widthParcentage = 90.0f
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_preview)
        itemAssociatedDTOS = ArrayList()
        intentData
        initLayout()
        loadData()
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) == 0
        ) {
            createPDF()
            viewPdf()
            return
        }
        requestPermission(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.preview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        return if (itemId == R.id.open_in) {
            openPdfFile()
            true
        } else if (itemId == R.id.print) {
            printPDF()
            true
        } else if (itemId != R.id.share) {
            super.onOptionsItemSelected(menuItem)
        } else {
            sharePdfFile()
            true
        }
    }

    @SuppressLint("WrongConstant")
    private fun openPdfFile() {
        val intent = Intent("android.intent.action.VIEW")
        intent.setDataAndType(
            FileProvider.getUriForFile(
                this, applicationContext.packageName + ".provider", File(
                    rootDirectory + File.separator + "invoice.pdf"
                )
            ), "application/pdf"
        )
        intent.flags = 1
        startActivity(Intent.createChooser(intent, "Select Application."))
    }

    @SuppressLint("WrongConstant")
    private fun sharePdfFile() {
        val intent = Intent("android.intent.action.SEND")
        intent.type = "message/rfc822"
        intent.putExtra("android.intent.extra.SUBJECT", catalogDTO!!.catalogName)
        intent.putExtra(
            "android.intent.extra.STREAM", FileProvider.getUriForFile(
                this, applicationContext.packageName + ".provider", File(
                    rootDirectory + File.separator + "invoice.pdf"
                )
            )
        )
        intent.flags = 1
        startActivity(Intent.createChooser(intent, "Select Application."))
    }

    @SuppressLint("WrongConstant")
    private fun printPDF() {
        if (Build.VERSION.SDK_INT >= 19) {
            (getSystemService("print") as PrintManager).print(
                getString(R.string.app_name) + " Document",
                MyPrintDocumentAdapter(this, rootDirectory + File.separator + "invoice.pdf"),
                null
            )
        }
    }

    private fun loadData() {
        businessDTO = instance!!.businessInformation
        loadInvoiceItems()
    }

    private fun loadInvoiceItems() {
        itemAssociatedDTOS!!.clear()
        val invoiceItems = instance!!.getInvoiceItems(
            catalogDTO!!.id
        )
        if (invoiceItems != null && invoiceItems.size > 0) {
            itemAssociatedDTOS!!.addAll(invoiceItems)
        }
    }

    private val intentData: Unit
        private get() {
            catalogDTO = intent.getSerializableExtra(MyConstants.CATALOG_DTO) as CatalogDTO?
            this.settingsDTO = SettingsDTO.settingsDTO
        }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        supportActionBar!!.setTitle(R.string.preview)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        pdfView = findViewById<View>(R.id.pdfView) as PDFView
        currencySign = formatCurrency(this, this.settingsDTO!!.currencyFormat)
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr)
        } else if (iArr.size != 0 && iArr[0] == 0) {
            createPDF()
            viewPdf()
        }
    }

    private fun checkString(str: String?): Boolean {
        return !TextUtils.isEmpty(str)
    }

    private fun createPDF() {
        var fileOutputStream: FileOutputStream? = null
        var e: Exception
        var fileOutputStream2: FileOutputStream? = null
        var th: Throwable?
        var e2: Exception?
        var th2: Throwable?
        var fileOutputStream3: FileOutputStream
        var e3: Exception
        var document: Document
        var e4: Exception?
        var th3: Throwable?
        var th4: Throwable?
        val pdfPCell: PdfPCell
        var pdfPTable: PdfPTable
        var pdfPTable2: PdfPTable
        var paragraph: Paragraph
        var pdfPCell2: PdfPCell
        var i: Int
        var d: Double
        var invoiceShippingDTO: InvoiceShippingDTO? = null
        val lineSeparator = LineSeparator()
        lineSeparator.percentage = 90.0f
        lineSeparator.offset = 20.0f
        var document2: String? = null
        try {
            val fileOutputStream4 =
                FileOutputStream(File(rootDirectory + File.separator + "invoice.pdf"))
            try {
                var document3 = Document()
                try {
                    try {
                        try {
                            document3.setMargins(0.0f, 0.0f, 36.0f, 36.0f)
                            PdfWriter.getInstance(document3, fileOutputStream4)
                            document3.open()
                            textBaseFont = BaseFont.createFont(
                                "assets/FreeSans.ttf",
                                BaseFont.IDENTITY_H,
                                true
                            )
                            textFont = Font(textBaseFont, 14.0f)
                            if (businessDTO != null) {
                                try {
                                    val pdfPTable3 = PdfPTable(2)
                                    pdfPTable3.defaultCell.border = 0
                                    val pdfPTable4 = PdfPTable(1)
                                    pdfPTable4.defaultCell.border = 0
                                    val pdfPTable5 = PdfPTable(1)
                                    pdfPTable5.defaultCell.border = 0
                                    if (checkString(businessDTO!!.name)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    businessDTO!!.name,
                                                    titleFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.regNo)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    "Registration Number : " + businessDTO!!.regNo,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.line1)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    businessDTO!!.line1,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.line2)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    businessDTO!!.line2,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.line3)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    businessDTO!!.line3,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.phoneNo)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    "Phone: " + businessDTO!!.phoneNo,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.mobileNo)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    "Mobile: " + businessDTO!!.mobileNo,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.fax)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    "Fax: " + businessDTO!!.fax,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.email)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    businessDTO!!.email,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.website)) {
                                        pdfPTable4.addCell(
                                            PdfPCell(
                                                Paragraph(
                                                    businessDTO!!.website,
                                                    textFont
                                                )
                                            )
                                        ).border =
                                            0
                                    }
                                    if (checkString(businessDTO!!.logo)) {
                                        if (File(businessDTO!!.logo).exists()) {
                                            val instance = Image.getInstance(businessDTO!!.logo)
                                            val pdfPTable6 = PdfPTable(2)
                                            val pdfPCell3 = PdfPCell()
                                            pdfPCell3.border = 0
                                            val pdfPCell4 = PdfPCell(instance, true)
                                            pdfPCell4.border = 0
                                            pdfPTable6.addCell(pdfPCell3)
                                            pdfPTable6.addCell(pdfPCell4)
                                            pdfPTable5.addCell(pdfPTable6)
                                        } else {
                                            pdfPTable5.addCell(PdfPCell()).border = 0
                                        }
                                    }
                                    pdfPTable3.addCell(pdfPTable4)
                                    pdfPTable3.addCell(pdfPTable5)
                                    pdfPTable3.widthPercentage = 90.0f
                                    pdfPTable3.spacingAfter = 40.0f
                                    document3.add(pdfPTable3)
                                    document3.add(lineSeparator)
                                } catch (e5: Exception) {
                                    e3 = e5
                                    fileOutputStream3 = fileOutputStream4
                                    document = document3
                                    try {
                                        e3.printStackTrace()
                                        document.close()
                                    } catch (th5: Throwable) {
                                        try {
                                            document.close()
                                            fileOutputStream3.close()
                                            throw th5
                                        } catch (e6: Exception) {
                                            e6.printStackTrace()
                                            throw th5
                                        }
                                    }
                                } catch (th6: Throwable) {
                                    th3 = th6
                                    fileOutputStream3 = fileOutputStream4
                                    document = document3
                                    document.close()
                                    fileOutputStream3.close()
                                    throw th3
                                }
                            }
                            val pdfPTable7 = PdfPTable(3)
                            pdfPTable7.defaultCell.border = 0
                            val pdfPTable8 = PdfPTable(1)
                            pdfPTable8.defaultCell.border = 0
                            val pdfPTable9 = PdfPTable(1)
                            pdfPTable9.defaultCell.border = 0
                            val pdfPTable10 = PdfPTable(1)
                            pdfPTable10.defaultCell.border = 0
                            if (catalogDTO!!.clientDTO.id > 0) {
                                try {
                                    val clientDTO = catalogDTO!!.clientDTO
                                    val pdfPCell5 = PdfPCell()
                                    pdfPCell5.horizontalAlignment = 0
                                    pdfPCell5.border = 0
                                    fileOutputStream3 = fileOutputStream4
                                    try {
                                        pdfPCell5.addElement(Paragraph("Bill To", titleFont))
                                        pdfPTable8.addCell(pdfPCell5)
                                        val pdfPCell6 = PdfPCell(Paragraph(clientDTO.clientName))
                                        pdfPCell6.horizontalAlignment = 0
                                        pdfPCell6.border = 0
                                        pdfPTable8.addCell(pdfPCell6)
                                        if (checkString(clientDTO.contactAdress)) {
                                            val pdfPCell7 =
                                                PdfPCell(Paragraph(clientDTO.contactAdress))
                                            pdfPCell7.horizontalAlignment = 0
                                            pdfPCell7.border = 0
                                            pdfPTable8.addCell(pdfPCell7)
                                        }
                                        if (checkString(clientDTO.addressLine1)) {
                                            val pdfPCell8 =
                                                PdfPCell(Paragraph(clientDTO.addressLine1))
                                            pdfPCell8.horizontalAlignment = 0
                                            pdfPCell8.border = 0
                                            pdfPTable8.addCell(pdfPCell8)
                                        }
                                        if (checkString(clientDTO.addressLine2)) {
                                            val pdfPCell9 =
                                                PdfPCell(Paragraph(clientDTO.addressLine2))
                                            pdfPCell9.horizontalAlignment = 0
                                            pdfPCell9.border = 0
                                            pdfPTable8.addCell(pdfPCell9)
                                        }
                                        if (checkString(clientDTO.addressLine3)) {
                                            val pdfPCell10 =
                                                PdfPCell(Paragraph(clientDTO.addressLine3))
                                            pdfPCell10.horizontalAlignment = 0
                                            pdfPCell10.border = 0
                                            pdfPTable8.addCell(pdfPCell10)
                                        }
                                        if (checkString(clientDTO.emailAddress)) {
                                            val pdfPCell11 =
                                                PdfPCell(Paragraph(clientDTO.emailAddress))
                                            pdfPCell11.horizontalAlignment = 0
                                            pdfPCell11.border = 0
                                            pdfPTable8.addCell(pdfPCell11)
                                        }
                                        if (checkString(clientDTO.shippingAddress) || checkString(
                                                clientDTO.shippingLine1
                                            ) || checkString(clientDTO.shippingLine2) || checkString(
                                                clientDTO.shippingLine3
                                            )
                                        ) {
                                            val pdfPCell12 = PdfPCell()
                                            pdfPCell12.horizontalAlignment = 0
                                            pdfPCell12.border = 0
                                            pdfPCell12.addElement(Paragraph("Ship To", titleFont))
                                            pdfPTable9.addCell(pdfPCell12)
                                        }
                                        if (checkString(clientDTO.shippingAddress)) {
                                            val pdfPCell13 = PdfPCell(
                                                Paragraph(
                                                    clientDTO.shippingAddress,
                                                    textFont
                                                )
                                            )
                                            pdfPCell13.horizontalAlignment = 0
                                            pdfPCell13.border = 0
                                            pdfPTable9.addCell(pdfPCell13)
                                        }
                                        if (checkString(clientDTO.shippingLine1)) {
                                            val pdfPCell14 = PdfPCell(
                                                Paragraph(
                                                    clientDTO.shippingLine1,
                                                    textFont
                                                )
                                            )
                                            pdfPCell14.horizontalAlignment = 0
                                            pdfPCell14.border = 0
                                            pdfPTable9.addCell(pdfPCell14)
                                        }
                                        if (checkString(clientDTO.shippingLine2)) {
                                            val pdfPCell15 = PdfPCell(
                                                Paragraph(
                                                    clientDTO.shippingLine2,
                                                    textFont
                                                )
                                            )
                                            pdfPCell15.horizontalAlignment = 0
                                            pdfPCell15.border = 0
                                            pdfPTable9.addCell(pdfPCell15)
                                        }
                                        if (checkString(clientDTO.shippingLine3)) {
                                            val pdfPCell16 = PdfPCell(
                                                Paragraph(
                                                    clientDTO.shippingLine3,
                                                    textFont
                                                )
                                            )
                                            pdfPCell16.horizontalAlignment = 0
                                            pdfPCell16.border = 0
                                            pdfPTable9.addCell(pdfPCell16)
                                        }
                                    } catch (e7: Exception) {
                                        e = e7
                                        e3 = e
                                        document = document3
                                        e3.printStackTrace()
                                        document.close()
                                    } catch (th7: Throwable) {
                                        th = th7
                                        th3 = th
                                        document = document3
                                        document.close()
                                        fileOutputStream3.close()
                                        throw th3
                                    }
                                } catch (e8: Exception) {
                                    e = e8
                                    fileOutputStream3 = fileOutputStream4
                                    e3 = e
                                    document = document3
                                    e3.printStackTrace()
                                    document.close()
                                } catch (th8: Throwable) {
                                    th = th8
                                    fileOutputStream3 = fileOutputStream4
                                    th3 = th
                                    document = document3
                                    document.close()
                                    fileOutputStream3.close()
                                    throw th3
                                }
                            } else {
                                fileOutputStream3 = fileOutputStream4
                            }
                            try {
                                var str = "Invoice    "
                                var catalogName = catalogDTO!!.catalogName
                                if (MyConstants.CATALOG_TYPE == 1) {
                                    str = "Estimate    "
                                }
                                if (catalogDTO!!.id == 0L) {
                                    catalogName = invoiceName
                                }
                                val pdfPCell17 = PdfPCell(Paragraph(str + catalogName, titleFont))
                                pdfPCell17.horizontalAlignment = 2
                                pdfPCell17.border = 0
                                pdfPTable10.addCell(pdfPCell17)
                                var formatDate = formatDate(
                                    this,
                                    catalogDTO!!.creationDate!!.toLong(),
                                    settingsDTO!!.dateFormat
                                )
                                if (catalogDTO!!.id == 0L) {
                                    formatDate = formatDate(
                                        this,
                                        Calendar.getInstance().timeInMillis,
                                        settingsDTO!!.dateFormat
                                    )
                                }
                                val pdfPCell18 = PdfPCell(Paragraph("Date   $formatDate", textFont))
                                pdfPCell18.horizontalAlignment = 2
                                pdfPCell18.border = 0
                                pdfPTable10.addCell(pdfPCell18)
                                if (catalogDTO!!.terms != 0 || catalogDTO!!.id == 0L) {
                                    var str2 = "Due   "
                                    if (catalogDTO!!.terms > 1) {
                                        str2 = str2 + formatDate(
                                            this,
                                            catalogDTO!!.dueDate!!.toLong(),
                                            settingsDTO!!.dateFormat
                                        )
                                    }
                                    if (catalogDTO!!.terms == 1 || catalogDTO!!.id == 0L) {
                                        str2 = "Terms   Due on receipt"
                                    }
                                    val pdfPCell19 = PdfPCell(Paragraph(str2, textFont))
                                    pdfPCell19.horizontalAlignment = 2
                                    pdfPCell19.border = 0
                                    pdfPTable10.addCell(pdfPCell19)
                                }
                                val poNumber = catalogDTO!!.poNumber
                                if (poNumber != null && poNumber != "") {
                                    val pdfPCell20 =
                                        PdfPCell(Paragraph("PO #   $poNumber", textFont))
                                    pdfPCell20.horizontalAlignment = 2
                                    pdfPCell20.border = 0
                                    pdfPTable10.addCell(pdfPCell20)
                                }
                                pdfPTable7.addCell(pdfPTable8)
                                pdfPTable7.addCell(pdfPTable9)
                                pdfPTable7.addCell(pdfPTable10)
                                pdfPTable7.widthPercentage = 90.0f
                                document3.add(pdfPTable7)
                                if (catalogDTO!!.invoiceShippingDTO.id > 0 && invoiceShippingDTO!!.id > 0 && (invoiceShippingDTO.shippingDate != "" || invoiceShippingDTO.amount != 0.0 || invoiceShippingDTO.shipVia != "" || invoiceShippingDTO.tracking != "" || invoiceShippingDTO.fob != "")) {
                                    val generateShippingTable =
                                        generateShippingTable(invoiceShippingDTO!!)
                                    generateShippingTable.spacingBefore = 20.0f
                                    generateShippingTable.spacingAfter = 25.0f
                                    generateShippingTable.widthPercentage = 90.0f
                                    document3.add(generateShippingTable)
                                }
                                if (itemAssociatedDTOS!!.size > 0) {
                                    val generateItemsTable = generateItemsTable()
                                    generateItemsTable.spacingAfter = 35.0f
                                    generateItemsTable.widthPercentage = 90.0f
                                    document3.add(generateItemsTable)
                                    lineSeparator.offset = 10.0f
                                    document3.add(lineSeparator)
                                    var paragraph2 = Paragraph()
                                    var pdfPTable11 = PdfPTable(2)
                                    pdfPTable11.widthPercentage = 100.0f
                                    val pdfPCell21 = PdfPCell()
                                    val pdfPTable12 = PdfPTable(1)
                                    pdfPTable12.setWidths(floatArrayOf(10.0f))
                                    val pdfPCell22 = PdfPCell()
                                    val paragraph3 = Paragraph()
                                    if (catalogDTO != null) {
                                        Log.e("ddddd notr", "  " + catalogDTO!!.notes)
                                        paragraph3.add(catalogDTO!!.notes)
                                        paragraph3.font = textFont
                                        pdfPCell22.addElement(paragraph3)
                                    }
                                    pdfPCell22.border = 0
                                    pdfPCell22.paddingLeft = 5.0f
                                    pdfPTable12.addCell(pdfPCell22)
                                    pdfPCell21.addElement(pdfPTable12)
                                    pdfPTable11.addCell(pdfPCell21)
                                    var pdfPTable13 = PdfPTable(2)
                                    pdfPTable13.setWidths(floatArrayOf(3.0f, 3.0f))
                                    val pdfPCell23 = PdfPCell()
                                    var i2 = 0
                                    var d2 = 0.0
                                    var i3 = 0
                                    while (i3 < 14) {
                                        val pdfPCell24 = PdfPCell()
                                        try {
                                            pdfPCell24.border = 0
                                            paragraph3.clear()
                                            when (i3) {
                                                0 -> {
                                                    paragraph = paragraph2
                                                    pdfPCell2 = pdfPCell24
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    Log.e("suntotal", "suntotal")
                                                    paragraph3.add("Subtotal")
                                                }
                                                1 -> {
                                                    paragraph = paragraph2
                                                    pdfPCell2 = pdfPCell24
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    Log.e("currency_sign", "currency_sign")
                                                    paragraph3.add(
                                                        currencySign + formatDecimal(
                                                            java.lang.Double.valueOf(
                                                                catalogDTO!!.subTotalAmount
                                                            )
                                                        )
                                                    )
                                                }
                                                2 -> {
                                                    paragraph = paragraph2
                                                    pdfPCell2 = pdfPCell24
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    i = catalogDTO!!.discountType
                                                    if (!(i == 0 || i == 1)) {
                                                        var str3 = "Discount"
                                                        d = formatDecimal(
                                                            java.lang.Double.valueOf(
                                                                catalogDTO!!.discountAmount
                                                            )
                                                        )
                                                        if (i == 2) {
                                                            str3 = "Discount (" + formatDecimal(
                                                                java.lang.Double.valueOf(
                                                                    catalogDTO!!.discount
                                                                )
                                                            ) + "%)"
                                                        }
                                                        paragraph3.add(str3)
                                                        i2 = i
                                                        d2 = d
                                                        break
                                                    }
                                                    i2 = i
                                                }
                                                3 -> {
                                                    paragraph = paragraph2
                                                    pdfPCell2 = pdfPCell24
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    if (!(i2 == 0 || i2 == 1)) {
                                                        paragraph3.add(currencySign + d2)
                                                        break
                                                    }
                                                }
                                                4 -> {
                                                    paragraph = paragraph2
                                                    pdfPCell2 = pdfPCell24
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    i = catalogDTO!!.taxType
                                                    if (i != 3) {
                                                        var str4 = "Tax"
                                                        d = formatDecimal(
                                                            java.lang.Double.valueOf(
                                                                catalogDTO!!.taxAmount
                                                            )
                                                        )
                                                        if (i == 0 || i == 1) {
                                                            str4 = "Tax (" + formatDecimal(
                                                                java.lang.Double.valueOf(
                                                                    catalogDTO!!.taxRate
                                                                )
                                                            ) + "%)"
                                                        }
                                                        paragraph3.add(str4)
                                                        i2 = i
                                                        d2 = d
                                                        break
                                                    }
                                                    i2 = i
                                                }
                                                5 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    if (i2 != 3) {
                                                        val sb = java.lang.StringBuilder()
                                                        sb.append(currencySign)
                                                        pdfPCell2 = pdfPCell24
                                                        sb.append(
                                                            formatDecimal(
                                                                java.lang.Double.valueOf(
                                                                    catalogDTO!!.taxAmount
                                                                )
                                                            )
                                                        )
                                                        paragraph3.add(sb.toString())
                                                        break
                                                    }
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                6 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add("Shipping")
                                                    Log.e("Shipping", "Shipping")
                                                    pdfPCell24.border = 2
                                                    pdfPCell24.paddingBottom = 10.0f
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                7 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add(
                                                        currencySign + formatDecimal(
                                                            java.lang.Double.valueOf(
                                                                catalogDTO!!.invoiceShippingDTO.amount
                                                            )
                                                        )
                                                    )
                                                    pdfPCell24.border = 2
                                                    pdfPCell24.paddingBottom = 10.0f
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                8 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add("Total")
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                9 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add(
                                                        currencySign + formatDecimal(
                                                            catalogDTO!!.totalAmount.toDouble()
                                                        )
                                                    )
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                10 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add("Paid")
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                11 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add(
                                                        currencySign + formatDecimal(
                                                            catalogDTO!!.paidAmount.toDouble()
                                                        )
                                                    )
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                12 -> {
                                                    paragraph = paragraph2
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    paragraph3.add("Balance Due")
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                13 -> {
                                                    val sb2 = java.lang.StringBuilder()
                                                    paragraph = paragraph2
                                                    sb2.append(currencySign)
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                    sb2.append(
                                                        formatDecimal(
                                                            java.lang.Double.valueOf(
                                                                (catalogDTO!!.totalAmount - catalogDTO!!.paidAmount).toDouble()
                                                            )
                                                        )
                                                    )
                                                    paragraph3.add(sb2.toString())
                                                    pdfPCell2 = pdfPCell24
                                                }
                                                else -> {
                                                    paragraph = paragraph2
                                                    pdfPCell2 = pdfPCell24
                                                    pdfPTable2 = pdfPTable11
                                                    pdfPTable = pdfPTable13
                                                }
                                            }
                                            pdfPCell2.addElement(paragraph3)
                                            pdfPTable.addCell(pdfPCell2)
                                            i3++
                                            pdfPTable13 = pdfPTable
                                            document3 = document3
                                            paragraph2 = paragraph
                                            pdfPTable11 = pdfPTable2
                                        } catch (e9: Exception) {
                                            e3 = e9
                                            document = document3
                                            e3.printStackTrace()
                                            document.close()
                                        } catch (th9: Throwable) {
                                            th3 = th9
                                            document = document3
                                            document.close()
                                            fileOutputStream3.close()
                                            throw th3
                                        }
                                    }
                                    try {
                                        pdfPCell23.addElement(pdfPTable13)
                                        pdfPTable11.addCell(pdfPCell23)
                                        pdfPTable11.widthPercentage = 90.0f
                                        pdfPTable11.spacingAfter = 20.0f
                                        paragraph2.add(pdfPTable11 as Element)
                                        document = document3
                                        try {
                                            try {
                                                document.add(paragraph2)
                                                val paragraph4 = Paragraph()
                                                val pdfPCell25 = PdfPCell()
                                                val pdfPCell26 = PdfPCell()
                                                val pdfPTable14 = PdfPTable(2)
                                                pdfPTable14.setWidths(floatArrayOf(5.0f, 5.0f))
                                                pdfPTable14.spacingAfter = 20.0f
                                                val pdfPTable15 = PdfPTable(1)
                                                val paragraph5 = Paragraph()
                                                paragraph5.font = textFont
                                                for (i4 in 0..4) {
                                                    val pdfPCell27 = PdfPCell()
                                                    pdfPCell27.border = 0
                                                    paragraph5.clear()
                                                    paragraph5.font = textFont
                                                    if (i4 != 0) {
                                                        if (i4 != 1) {
                                                            if (i4 != 2) {
                                                                if (i4 != 3) {
                                                                    if (i4 == 4 && !TextUtils.isEmpty(
                                                                            businessDTO!!.otherPaymentInformation
                                                                        )
                                                                    ) {
                                                                        paragraph5.add(
                                                                            """
                                                                            Other
                                                                            ${businessDTO!!.otherPaymentInformation}
                                                                            """.trimIndent()
                                                                        )
                                                                    }
                                                                } else if (!TextUtils.isEmpty(
                                                                        businessDTO!!.checkInformation
                                                                    )
                                                                ) {
                                                                    paragraph5.add(
                                                                        """
                                                                        By cheque
                                                                        Make cheques payable to: ${businessDTO!!.checkInformation}
                                                                        """.trimIndent()
                                                                    )
                                                                }
                                                            } else if (!TextUtils.isEmpty(
                                                                    businessDTO!!.bankInformation
                                                                )
                                                            ) {
                                                                paragraph5.add(
                                                                    """
                                                                    Bank Transfer
                                                                    ${businessDTO!!.bankInformation}
                                                                    """.trimIndent()
                                                                )
                                                            }
                                                        } else if (!TextUtils.isEmpty(businessDTO!!.paypalAddress)) {
                                                            paragraph5.add(
                                                                """
                                                                Via PayPal
                                                                Send payment to: ${businessDTO!!.paypalAddress}
                                                                """.trimIndent()
                                                            )
                                                        }
                                                    } else if (!TextUtils.isEmpty(businessDTO!!.paypalAddress) || !TextUtils.isEmpty(
                                                            businessDTO!!.bankInformation
                                                        ) || !TextUtils.isEmpty(
                                                            businessDTO!!.checkInformation
                                                        ) || !TextUtils.isEmpty(
                                                            businessDTO!!.otherPaymentInformation
                                                        )
                                                    ) {
                                                        paragraph5.font = titleFont
                                                        paragraph5.add("Payment Instructions")
                                                    }
                                                    pdfPCell27.addElement(paragraph5)
                                                    pdfPTable15.addCell(pdfPCell27)
                                                }
                                                pdfPCell25.addElement(pdfPTable15)
                                                pdfPTable14.addCell(pdfPCell25).border = 0
                                                pdfPTable14.widthPercentage = 90.0f
                                                val pdfPTable16 = PdfPTable(1)
                                                if (catalogDTO!!.signedUrl == null && catalogDTO!!.signedDate == null) {
                                                    loadItemImages()
                                                    val paragraph6 = Paragraph()
                                                    paragraph6.font = textFont
                                                    val width = document.pageSize.width * 0.8f
                                                    for (i5 in imageDTOS.indices) {
                                                        if (File(imageDTOS[i5].imageUrl).exists()) {
                                                            pdfPTable16.addCell(PdfPCell()).border =
                                                                0
                                                        } else {
                                                            val instance2 = Image.getInstance(
                                                                imageDTOS[i5].imageUrl
                                                            )
                                                            val f = 0.5f * width
                                                            instance2.scaleToFit(
                                                                f,
                                                                Math.abs((instance2.width - f) / instance2.width) * instance2.height
                                                            )
                                                            instance2.spacingBefore = 20.0f
                                                            val pdfPCell28 = PdfPCell(instance2)
                                                            pdfPCell28.horizontalAlignment = 0
                                                            pdfPCell28.border = 0
                                                            pdfPTable16.addCell(pdfPCell28).border =
                                                                0
                                                        }
                                                        val description = imageDTOS[i5].description
                                                        val additionalDetails =
                                                            imageDTOS[i5].additionalDetails
                                                        val pdfPCell29 = PdfPCell()
                                                        paragraph6.clear()
                                                        paragraph6.add(description)
                                                        pdfPCell29.addElement(paragraph6)
                                                        pdfPCell29.border = 0
                                                        pdfPTable16.addCell(pdfPCell29)
                                                        val pdfPCell30 = PdfPCell()
                                                        paragraph6.clear()
                                                        paragraph6.add(additionalDetails)
                                                        pdfPCell30.addElement(paragraph6)
                                                        pdfPCell30.border = 0
                                                        pdfPTable16.addCell(pdfPCell30).border = 0
                                                    }
                                                    pdfPTable16.widthPercentage = 90.0f
                                                }
                                                if (catalogDTO!!.signedUrl != null) {
                                                    if (File(catalogDTO!!.signedUrl).exists()) {
                                                        pdfPCell = PdfPCell(
                                                            Image.getInstance(catalogDTO!!.signedUrl),
                                                            true
                                                        )
                                                    } else {
                                                        pdfPCell = PdfPCell()
                                                        pdfPCell.horizontalAlignment = 2
                                                        pdfPCell.border = 0
                                                    }
                                                    pdfPTable16.addCell(pdfPCell)
                                                }
                                                if (catalogDTO!!.signedDate != null) {
                                                    val pdfPCell31 = PdfPCell(
                                                        Paragraph(
                                                            formatDate(
                                                                this,
                                                                catalogDTO!!.signedDate!!.toLong(),
                                                                settingsDTO!!.dateFormat
                                                            ),
                                                            textFont
                                                        )
                                                    )
                                                    pdfPCell31.border = 0
                                                    pdfPCell31.horizontalAlignment = 2
                                                    pdfPTable16.addCell(pdfPCell31)
                                                }
                                                pdfPCell26.addElement(pdfPTable16)
                                                pdfPTable14.addCell(pdfPCell26).border = 0
                                                pdfPTable14.widthPercentage = 90.0f
                                                paragraph4.add(pdfPTable14 as Element)
                                                document.add(paragraph4)
                                            } catch (e10: Exception) {
                                                e4 = e10
                                                e3 = e4
                                                e3.printStackTrace()
                                                document.close()
                                            }
                                        } catch (th10: Throwable) {
                                            th4 = th10
                                            th3 = th4
                                            document.close()
                                            fileOutputStream3.close()
                                            throw th3
                                        }
                                    } catch (e11: Exception) {
                                        e4 = e11
                                        document = document3
                                    } catch (th11: Throwable) {
                                        th4 = th11
                                        document = document3
                                    }
                                } else {
                                    document = document3
                                }
                                try {
                                    document.close()
                                    fileOutputStream3.close()
                                } catch (e12: Exception) {
                                    e12.printStackTrace()
                                }
                            } catch (e13: Exception) {
                                e4 = e13
                                document = document3
                                e3 = e4
                                e3.printStackTrace()
                                document.close()
                            } catch (th12: Throwable) {
                                th4 = th12
                                document = document3
                                th3 = th4
                                document.close()
                                fileOutputStream3.close()
                                throw th3
                            }
                        } catch (e14: Exception) {
                            e4 = e14
                            fileOutputStream3 = fileOutputStream4
                        } catch (th13: Throwable) {
                            th4 = th13
                            fileOutputStream3 = fileOutputStream4
                        }
                    } catch (e15: Exception) {
                        e2 = e15
                        document2 = "currency_sign"
                        e = e2
                        e.printStackTrace()
                        if (document2 != null) {
//                            document2.close();
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close()
                            } catch (e16: IOException) {
                                e16.printStackTrace()
                            }
                        }
                    }
                } catch (th14: Throwable) {
                    th2 = th14
                    document2 = "currency_sign"
                    th = th2
                    if (document2 != null) {
//                        document2.close();
                    }
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close()
                        } catch (e17: IOException) {
                            e17.printStackTrace()
                        }
                    }
                    try {
                        throw th
                    } catch (th15: Throwable) {
                        th15.printStackTrace()
                    }
                }
            } catch (e18: Exception) {
                e2 = e18
                fileOutputStream = fileOutputStream4
            } catch (th16: Throwable) {
                th2 = th16
                fileOutputStream2 = fileOutputStream4
            }
        } catch (e19: Exception) {
            e = e19
            fileOutputStream = null
        } catch (th17: Throwable) {
            th = th17
            fileOutputStream2 = null
        }
    }

    private fun generateShippingTable(invoiceShippingDTO: InvoiceShippingDTO): PdfPTable {
        val pdfPTable = PdfPTable(5)
        for (i in 0..4) {
            val pdfPCell = PdfPCell()
            pdfPCell.border = 0
            pdfPCell.backgroundColor = BaseColor.GRAY
            pdfPCell.setPadding(0.0f)
            pdfPCell.minimumHeight = 40.0f
            if (i == 0) {
                pdfPCell.horizontalAlignment = 0
                pdfPCell.paddingLeft = 5.0f
                pdfPCell.addElement(Paragraph("SHIP DATE", normalWhite))
            } else if (i == 1) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.addElement(Paragraph("SHIP AMOU.", normalWhite))
            } else if (i == 2) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.addElement(Paragraph("SHIP VIA", normalWhite))
            } else if (i == 3) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.addElement(Paragraph("SHIP TRAC.", normalWhite))
            } else if (i == 4) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.addElement(Paragraph("SHIP FOB", normalWhite))
            }
            pdfPTable.addCell(pdfPCell)
        }
        for (i2 in 0..4) {
            val pdfPCell2 = PdfPCell()
            pdfPCell2.border = 0
            if (i2 == 0) {
                pdfPCell2.horizontalAlignment = 0
                pdfPCell2.paddingLeft = 5.0f
                pdfPCell2.addElement(Paragraph(invoiceShippingDTO.shippingDate, textFont))
            } else if (i2 == 1) {
                pdfPCell2.horizontalAlignment = 2
                pdfPCell2.addElement(
                    Paragraph(
                        currencySign + formatDecimal(java.lang.Double.valueOf(invoiceShippingDTO.amount)),
                        textFont
                    )
                )
            } else if (i2 == 2) {
                pdfPCell2.horizontalAlignment = 2
                pdfPCell2.addElement(Paragraph(invoiceShippingDTO.shipVia, textFont))
            } else if (i2 == 3) {
                pdfPCell2.horizontalAlignment = 2
                pdfPCell2.addElement(Paragraph(invoiceShippingDTO.tracking, textFont))
            } else if (i2 == 4) {
                pdfPCell2.horizontalAlignment = 2
                pdfPCell2.addElement(Paragraph(invoiceShippingDTO.fob, textFont))
            }
            pdfPTable.addCell(pdfPCell2)
        }
        return pdfPTable
    }

    private fun generateItemsTable(): PdfPTable {
        var i: Int
        var i2: Int
        var d: Double
        var discountType = catalogDTO!!.discountType
        var taxType = catalogDTO!!.taxType
        var i3 = 1
        var i4 = 2
        val i5 =
            if (discountType == 1 && taxType == 2) 6 else if (discountType == 1 || taxType == 2) 5 else 4
        val pdfPTable = PdfPTable(i5)
        pdfPTable.spacingBefore = 20.0f
        var i6 = 6
        var i7 = 0
        if (i5 == 6) {
            try {
                pdfPTable.setWidths(floatArrayOf(1.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f))
            } catch (e: DocumentException) {
                e.printStackTrace()
            }
        } else if (i5 == 5) {
            try {
                pdfPTable.setWidths(floatArrayOf(1.5f, 1.0f, 1.0f, 1.0f, 1.0f))
            } catch (e2: DocumentException) {
                e2.printStackTrace()
            }
        } else {
            try {
                pdfPTable.setWidths(floatArrayOf(3.0f, 1.0f, 1.0f, 1.0f))
            } catch (e3: DocumentException) {
                e3.printStackTrace()
            }
        }
        var i8 = 0
        var i9 = 6
        while (i8 < i9) {
            val pdfPCell = PdfPCell()
            pdfPCell.border = 0
            pdfPCell.backgroundColor = BaseColor(2, 154, 255)
            pdfPCell.minimumHeight = 20.0f
            if (i8 == 0) {
                pdfPCell.horizontalAlignment = 0
                pdfPCell.verticalAlignment = 5
                pdfPCell.paddingLeft = 5.0f
                pdfPCell.addElement(Paragraph("DESCRIPTION", normalWhite))
            } else if (i8 == 1) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.verticalAlignment = 5
                pdfPCell.addElement(
                    Paragraph(
                        Contract.ItemsAssociated.QTY,
                        normalWhite
                    )
                )
            } else if (i8 == 2) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.verticalAlignment = 5
                pdfPCell.addElement(Paragraph("RATE", normalWhite))
            }
            pdfPTable.addCell(pdfPCell)
            i8++
            i9 = 8
        }
        val arrayList = itemAssociatedDTOS!!
        var d2 = 0.0
        var d3 = 0.0
        var i10 = 0
        while (i10 < arrayList.size) {
            Log.e("arraylist", "= " + arrayList.size)
            var unitCost = arrayList[i10].unitCost * arrayList[i10].quantity
            if (discountType == i3) {
                d2 = arrayList[i10].discount / 100.0 * unitCost
                unitCost -= d2
            }
            if (taxType == i4) {
                d3 = arrayList[i10].taxRate / 100.0 * unitCost
            }
            var d4 = d3
            arrayList[i10].totalAmount = formatDecimal(java.lang.Double.valueOf(unitCost))
            var i11 = 0
            var i12 = 8
            while (i11 < i12) {
                val pdfPCell2 = PdfPCell()
                pdfPCell2.border = i7
                if (i11 == 0) {
                    i2 = discountType
                    d = d4
                    i = taxType
                    pdfPCell2.horizontalAlignment = 0
                    pdfPCell2.paddingLeft = 5.0f
                    val paragraph = Paragraph(
                        """
                              ${arrayList[i10].itemName}
                              ${arrayList[i10].description}
                              """.trimIndent(), textFont
                    )
                    paragraph.setLeading(0.0f, 1.0f)
                    pdfPCell2.addElement(paragraph)
                } else if (i11 == i3) {
                    i2 = discountType
                    d = d4
                    i = taxType
                    pdfPCell2.horizontalAlignment = 2
                    pdfPCell2.addElement(
                        Paragraph(
                            java.lang.String.valueOf(arrayList[i10].quantity),
                            textFont
                        )
                    )
                } else if (i11 != i4) {
                    if (i11 != i6) {
                        if (i11 == 4) {
                            d = d4
                            pdfPCell2.horizontalAlignment = 2
                            pdfPCell2.addElement(
                                Paragraph(
                                    currencySign + arrayList[i10].totalAmount,
                                    textFont
                                )
                            )
                            i2 = discountType
                        } else if (i11 != 5) {
                            i2 = discountType
                            d = d4
                        } else {
                            if (taxType == i4) {
                                pdfPCell2.horizontalAlignment = i4
                                if (arrayList[i10].taxAble != 0) {
                                    val sb = StringBuilder()
                                    sb.append(currencySign)
                                    d = d4
                                    sb.append(d)
                                    sb.append(" (")
                                    sb.append(arrayList[i10].taxRate)
                                    sb.append("%)")
                                    pdfPCell2.addElement(Paragraph(sb.toString(), textFont))
                                    d2 = d2
                                    i2 = discountType
                                } else {
                                    d = d4
                                    pdfPCell2.addElement(Paragraph(currencySign + "0", textFont))
                                }
                            } else {
                                d = d4
                            }
                            i2 = discountType
                            i = taxType
                        }
                        i = taxType
                    } else {
                        d = d4
                        if (discountType == 1) {
                            pdfPCell2.horizontalAlignment = 2
                            val sb2 = StringBuilder()
                            sb2.append(currencySign)
                            d2 = d2
                            sb2.append(d2)
                            sb2.append(" (")
                            i2 = discountType
                            i = taxType
                            sb2.append(arrayList[i10].discount)
                            sb2.append("%)")
                            pdfPCell2.addElement(Paragraph(sb2.toString(), textFont))
                        } else {
                            d2 = d2
                            i2 = discountType
                            i = taxType
                        }
                    }
                    i11++
                    discountType = i2
                    taxType = i
                    i12 = 6
                    i6 = 3
                    i7 = 0
                    d4 = d
                    i3 = 1
                    i4 = 2
                } else {
                    i2 = discountType
                    d = d4
                    i = taxType
                    pdfPCell2.horizontalAlignment = 2
                    pdfPCell2.addElement(
                        Paragraph(
                            currencySign + arrayList[i10].unitCost,
                            textFont
                        )
                    )
                }
                pdfPTable.addCell(pdfPCell2)
                i11++
                discountType = i2
                taxType = i
                i12 = 6
                i6 = 3
                i7 = 0
                d4 = d
                i3 = 1
                i4 = 2
            }
            i10++
            d3 = d4
            taxType = taxType
            i3 = 1
            i4 = 2
            i6 = 3
            i7 = 0
        }
        return pdfPTable
    }

    private fun loadItemImages() {
        imageDTOS.clear()
        val invoiceItemImages = instance!!.getInvoiceItemImages(
            catalogDTO!!.id
        )
        if (invoiceItemImages != null && invoiceItemImages.size > 0) {
            imageDTOS.addAll(invoiceItemImages)
        }
    }

    private fun viewPdf() {
        val rootDirectory = rootDirectory
        pdfFileName = rootDirectory + File.separator + "invoice.pdf"
        pdfView!!.fromFile(File(pdfFileName)).defaultPage(pageNumber).enableSwipe(true)
            .swipeHorizontal(false).onPageChange(this).enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(this)).pageFitPolicy(FitPolicy.BOTH).spacing(10)
            .load()
        pdfView!!.maxZoom = 1.0f
    }

    override fun onPageChanged(i: Int, i2: Int) {
        pageNumber = Integer.valueOf(i)
        title = String.format(
            "%s %s / %s",
            pdfFileName, Integer.valueOf(i + 1), Integer.valueOf(i2)
        )
    }

    override fun loadComplete(i: Int) {
        printBookmarksTree(pdfView!!.tableOfContents, "-")
    }

    fun printBookmarksTree(list: List<Bookmark>, str: String) {
        for (bookmark in list) {
            if (bookmark.hasChildren()) {
                val children = bookmark.children
                printBookmarksTree(children, "$str-")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun start(context: Context, catalogDTO2: CatalogDTO?) {
            val intent = Intent(context, InvoicePreviewActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, catalogDTO2)
            context.startActivity(intent)
        }

        private fun requestPermission(context: Context) {
            val activity = context as Activity
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                )
            ) {
                AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.permission_storage))
                    .setPositiveButton(
                        "ok"
                    ) { dialogInterface, i ->
                        /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoicePreviewActivity.AnonymousClass2 */
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                            101
                        )
                    }.setNegativeButton(
                        "no"
                    ) { dialogInterface, i ->
                        /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.InvoicePreviewActivity.AnonymousClass1 */
                        dialogInterface.dismiss()
                    }.show()
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                    101
                )
            }
        }
    }
}