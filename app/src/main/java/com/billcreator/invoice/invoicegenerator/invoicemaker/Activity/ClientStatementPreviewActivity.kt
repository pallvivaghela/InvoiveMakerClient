package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import android.text.TextUtils
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
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.BusinessDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.CatalogDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
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

class ClientStatementPreviewActivity : AppCompatActivity(), OnPageChangeListener,
    OnLoadCompleteListener {
    private var businessDTO: BusinessDTO? = null
    private var catalogs: ArrayList<CatalogDTO>? = null
    private var clientDTO: ClientDTO? = null
    private var currencySign: String? = null
    var pdfFileName: String? = null
    var pdfView: PDFView? = null
    private var settingsDTO: SettingsDTO? = null
    var textBaseFont: BaseFont? = null
    private var toolbar: Toolbar? = null
    var normalWhite = Font(Font.FontFamily.HELVETICA, 12.0f, 0, BaseColor.WHITE)
    var pageNumber = 0
    var textFont = Font(Font.FontFamily.TIMES_ROMAN, 14.0f)
    var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 18.0f, 1, BaseColor.BLACK)
    private val widthParcentage = 90.0f


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_preview)
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                this,
                applicationContext.packageName + ".provider",
                File(MyConstants.rootDirectory + File.separator + "Client_Report.pdf")
            ), "application/pdf"
        )
        intent.flags = 1
        startActivity(Intent.createChooser(intent, "Select Application."))
    }

    @SuppressLint("WrongConstant")
    private fun sharePdfFile() {
        val intent = Intent("android.intent.action.SEND")
        intent.type = "message/rfc822"
        intent.putExtra("android.intent.extra.SUBJECT", clientDTO!!.clientName)
        intent.putExtra(
            "android.intent.extra.STREAM",
            FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                File(MyConstants.rootDirectory + File.separator + "Client_Report.pdf")
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
                MyPrintDocumentAdapter(
                    this,
                    MyConstants.rootDirectory + File.separator + "Client_Report.pdf"
                ),
                null
            )
        }
    }

    private fun loadData() {
        businessDTO = LoadDatabase.instance!!.businessInformation
        try {
            loadInvoiceItems()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInvoiceItems() {
        catalogs = LoadDatabase.instance!!.getCatalogsByClient(clientDTO!!.id, 1, 0)
    }

    private val intentData: Unit
        private get() {
            clientDTO = intent.getSerializableExtra(MyConstants.CLIENT_DTO) as ClientDTO?
            settingsDTO = SettingsDTO.settingsDTO
        }

    private fun initLayout() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        this.toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Client Report"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        pdfView = findViewById<View>(R.id.pdfView) as PDFView
        currencySign = MyConstants.formatCurrency(this, settingsDTO!!.currencyFormat)
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr)
        } else if (iArr.size != 0 && iArr[0] == 0) {
            createPDF()
        }
    }

    private fun checkString(str: String?): Boolean {
        return !TextUtils.isEmpty(str)
    }

    private fun createPDF() {
        var document: Document?
        var e: Exception
        var fileOutputStream: FileOutputStream?
        var th: Throwable?
        var e2: Exception
        val lineSeparator = LineSeparator()
        lineSeparator.percentage = 90.0f
        lineSeparator.offset = 20.0f
        try {
            val rootDirectory = MyConstants.rootDirectory
            val fileOutputStream2 =
                FileOutputStream(File(rootDirectory + File.separator + "Client_Report.pdf"))
            try {
                try {
                    document = Document()
                    try {
                        try {
                            document.setMargins(0.0f, 0.0f, 36.0f, 36.0f)
                            PdfWriter.getInstance(document, fileOutputStream2)
                            document.open()
                            textBaseFont = BaseFont.createFont(
                                "assets/FreeSans.ttf",
                                BaseFont.IDENTITY_H,
                                true
                            )
                            textFont = Font(textBaseFont, 14.0f)
                            if (businessDTO != null) {
                                val pdfPTable = PdfPTable(2)
                                pdfPTable.defaultCell.border = 0
                                val pdfPTable2 = PdfPTable(1)
                                pdfPTable2.defaultCell.border = 0
                                val pdfPTable3 = PdfPTable(1)
                                pdfPTable3.defaultCell.border = 0
                                if (checkString(businessDTO!!.name)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                businessDTO!!.name,
                                                titleFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.regNo)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                "Registration Number : " + businessDTO!!.regNo,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.line1)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                businessDTO!!.line1,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.line2)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                businessDTO!!.line2,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.line3)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                businessDTO!!.line3,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.phoneNo)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                "Phone: " + businessDTO!!.phoneNo,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.mobileNo)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                "Mobile: " + businessDTO!!.mobileNo,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.fax)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                "Fax: " + businessDTO!!.fax,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.email)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                businessDTO!!.email,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.website)) {
                                    pdfPTable2.addCell(
                                        PdfPCell(
                                            Paragraph(
                                                businessDTO!!.website,
                                                textFont
                                            )
                                        )
                                    ).border = 0
                                }
                                if (checkString(businessDTO!!.logo)) {
                                    val instance = Image.getInstance(
                                        businessDTO!!.logo
                                    )
                                    val pdfPTable4 = PdfPTable(2)
                                    val pdfPCell = PdfPCell()
                                    pdfPCell.border = 0
                                    val pdfPCell2 = PdfPCell(instance, true)
                                    pdfPCell2.border = 0
                                    pdfPTable4.addCell(pdfPCell)
                                    pdfPTable4.addCell(pdfPCell2)
                                    pdfPTable3.addCell(pdfPTable4)
                                }
                                pdfPTable.addCell(pdfPTable2)
                                pdfPTable.addCell(pdfPTable3)
                                pdfPTable.widthPercentage = 90.0f
                                pdfPTable.spacingAfter = 40.0f
                                document.add(pdfPTable)
                                document.add(lineSeparator)
                            }
                            val pdfPTable5 = PdfPTable(2)
                            pdfPTable5.defaultCell.border = 0
                            val pdfPTable6 = PdfPTable(1)
                            pdfPTable6.defaultCell.border = 0
                            val pdfPTable7 = PdfPTable(1)
                            pdfPTable7.defaultCell.border = 0
                            val pdfPCell3 = PdfPCell(Paragraph("Client", titleFont))
                            pdfPCell3.horizontalAlignment = 0
                            pdfPCell3.border = 0
                            pdfPTable6.addCell(pdfPCell3)
                            val pdfPCell4 =
                                PdfPCell(Paragraph("" + clientDTO!!.clientName, textFont))
                            pdfPCell4.horizontalAlignment = 0
                            pdfPCell4.border = 0
                            pdfPTable6.addCell(pdfPCell4)
                            val pdfPCell5 = PdfPCell(Paragraph("Statement of Account", titleFont))
                            pdfPCell5.horizontalAlignment = 2
                            pdfPCell5.border = 0
                            pdfPTable7.addCell(pdfPCell5)
                            val formatDate = MyConstants.formatDate(
                                this,
                                Calendar.getInstance().timeInMillis,
                                settingsDTO!!.dateFormat
                            )
                            val pdfPCell6 = PdfPCell(Paragraph("Date   $formatDate", textFont))
                            pdfPCell6.horizontalAlignment = 2
                            pdfPCell6.border = 0
                            pdfPTable7.addCell(pdfPCell6)
                            pdfPTable5.addCell(pdfPTable6)
                            pdfPTable5.addCell(pdfPTable7)
                            pdfPTable5.widthPercentage = 90.0f
                            document.add(pdfPTable5)
                            val generateInvoiceTable = generateInvoiceTable()
                            generateInvoiceTable.spacingBefore = 20.0f
                            if (catalogs!!.size > 0) {
                                generateInvoiceTable.spacingAfter = 50.0f
                            } else {
                                generateInvoiceTable.spacingAfter = 20.0f
                            }
                            generateInvoiceTable.widthPercentage = 90.0f
                            document.add(generateInvoiceTable)
                            if (catalogs!!.size > 0) {
                                document.add(lineSeparator)
                            }
                            val pdfPTable8 = PdfPTable(2)
                            pdfPTable8.setWidths(floatArrayOf(7.0f, 3.0f))
                            pdfPTable8.spacingAfter = 20.0f
                            val pdfPTable9 = PdfPTable(1)
                            val pdfPTable10 = PdfPTable(2)
                            pdfPTable10.setWidths(floatArrayOf(1.5f, 1.5f))
                            for (i in 0..1) {
                                val pdfPCell7 = PdfPCell()
                                pdfPCell7.border = 0
                                pdfPCell7.setPadding(0.0f)
                                pdfPCell7.minimumHeight = 25.0f
                                if (i == 0) {
                                    pdfPCell7.horizontalAlignment = 0
                                    pdfPCell7.paddingLeft = 5.0f
                                    pdfPCell7.addElement(Paragraph("TOTAL", textFont))
                                } else if (i == 1) {
                                    pdfPCell7.horizontalAlignment = 2
                                    var d = 0.0
                                    for (i2 in catalogs!!.indices) {
                                        d += catalogs!![i2].totalAmount - catalogs!![i2].paidAmount
                                    }
                                    pdfPCell7.addElement(
                                        Paragraph(
                                            MyConstants.formatCurrency(
                                                this,
                                                settingsDTO!!.currencyFormat
                                            ) + MyConstants.formatDecimal(java.lang.Double.valueOf(d)),
                                            textFont
                                        )
                                    )
                                }
                                pdfPTable10.addCell(pdfPCell7)
                            }
                            val paragraph = Paragraph()
                            paragraph.font = textFont
                            for (i3 in 0..4) {
                                val pdfPCell8 = PdfPCell()
                                pdfPCell8.border = 0
                                paragraph.clear()
                                paragraph.font = textFont
                                if (i3 == 0) {
                                    paragraph.font = titleFont
                                    paragraph.add("Payment Instructions")
                                } else if (i3 == 1) {
                                    paragraph.add(
                                        """
    Via PayPal
    Send payment to: ${businessDTO!!.paypalAddress}
    """.trimIndent()
                                    )
                                } else if (i3 == 2) {
                                    paragraph.add(
                                        """
    Bank Transfer
    ${businessDTO!!.bankInformation}
    """.trimIndent()
                                    )
                                } else if (i3 == 3) {
                                    paragraph.add(
                                        """
    By cheque
    Make cheques payable to: ${businessDTO!!.checkInformation}
    """.trimIndent()
                                    )
                                } else if (i3 == 4) {
                                    paragraph.add(
                                        """
    Other
    ${businessDTO!!.otherPaymentInformation}
    """.trimIndent()
                                    )
                                }
                                pdfPCell8.addElement(paragraph)
                                pdfPTable9.addCell(pdfPCell8)
                            }
                            pdfPTable8.addCell(PdfPCell(pdfPTable9)).border = 0
                            pdfPTable8.addCell(PdfPCell(pdfPTable10)).border = 0
                            pdfPTable8.widthPercentage = 90.0f
                            document.add(pdfPTable8)
                            try {
                                document.close()
                                fileOutputStream2.close()
                            } catch (e3: Exception) {
                                e3.printStackTrace()
                            }
                        } catch (th2: Throwable) {
                            document.close()
                            fileOutputStream2.close()
                            throw th2
                        }
                    } catch (e4: Exception) {
                        try {
                            e4.printStackTrace()
                            document.close()
                        } catch (th3: Throwable) {
                            try {
                                try {
                                    document.close()
                                    fileOutputStream2.close()
                                    throw th3
                                } catch (e5: Exception) {
                                    e5.printStackTrace()
                                    throw th3
                                }
                            } catch (e6: Exception) {
                                e2 = e6
                                try {
                                    e2.printStackTrace()
                                    if (document != null) {
                                        document.close()
                                    }
                                } catch (e7: Exception) {
                                    e = e7
                                    e.printStackTrace()
                                    if (document != null) {
                                        document.close()
                                    }
                                    val fileOutputStream3: FileOutputStream? = null
                                    try {
                                        fileOutputStream3!!.close()
                                    } catch (e8: IOException) {
                                        e8.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                } catch (e9: Exception) {
                    e2 = e9
                    document = null
                }
            } catch (th4: Throwable) {
                th = th4
                fileOutputStream = fileOutputStream2
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e10: IOException) {
                        e10.printStackTrace()
                    }
                }
                try {
                    throw th
                } catch (th5: Throwable) {
                    th5.printStackTrace()
                }
            }
        } catch (e11: Exception) {
            e = e11
            document = null
        } catch (th6: Throwable) {
            th = th6
            fileOutputStream = null
        }
    }

    private fun generateInvoiceTable(): PdfPTable {
        val pdfPTable = PdfPTable(2)
        try {
            pdfPTable.setWidths(floatArrayOf(8.0f, 1.5f))
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
        for (i in 0..1) {
            val pdfPCell = PdfPCell()
            pdfPCell.border = 0
            pdfPCell.backgroundColor = BaseColor.GRAY
            pdfPCell.setPadding(0.0f)
            pdfPCell.minimumHeight = 25.0f
            if (i == 0) {
                pdfPCell.horizontalAlignment = 0
                pdfPCell.paddingLeft = 5.0f
                pdfPCell.addElement(Paragraph("INVOICE", normalWhite))
            } else if (i == 1) {
                pdfPCell.horizontalAlignment = 2
                pdfPCell.addElement(Paragraph("OWING", normalWhite))
            }
            pdfPTable.addCell(pdfPCell)
        }
        for (i2 in catalogs!!.indices) {
            val totalAmount = catalogs!![i2].totalAmount - catalogs!![i2].paidAmount
            for (i3 in 0..1) {
                val pdfPCell2 = PdfPCell()
                pdfPCell2.border = 0
                if (i3 == 0) {
                    pdfPCell2.horizontalAlignment = 0
                    pdfPCell2.paddingLeft = 5.0f
                    var str = MyConstants.formatDate(
                        this,
                        catalogs!![i2].creationDate!!.toLong(),
                        settingsDTO!!.dateFormat
                    ) + " - " + catalogs!![i2].catalogName
                    if (catalogs!![i2].paidAmount > 0.0) {
                        str = """
     $str
     ${MyConstants.formatCurrency(this, settingsDTO!!.currencyFormat)}
     """.trimIndent() + MyConstants.formatDecimal(
                            catalogs!![i2].totalAmount.toDouble()

                        ) + " Total"
                    }
                    val paragraph = Paragraph(str, textFont)
                    paragraph.setLeading(0.0f, 1.0f)
                    pdfPCell2.addElement(paragraph)
                } else if (i3 == 1) {
                    pdfPCell2.horizontalAlignment = 2
                    pdfPCell2.addElement(
                        Paragraph(
                            MyConstants.formatCurrency(
                                this,
                                settingsDTO!!.currencyFormat
                            ) + MyConstants.formatDecimal(totalAmount.toDouble()),
                            textFont
                        )
                    )
                }
                pdfPTable.addCell(pdfPCell2)
            }
        }
        return pdfPTable
    }

    private fun viewPdf() {
        val rootDirectory = MyConstants.rootDirectory
        pdfFileName = rootDirectory + File.separator + "Client_Report.pdf"
        pdfView!!.fromFile(File(pdfFileName)).defaultPage(pageNumber).enableSwipe(true)
            .swipeHorizontal(false).onPageChange(this).enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(this)).spacing(10).load()
        pdfView!!.maxZoom = 1.0f
    }


    override fun onPageChanged(i: Int, i2: Int) {
        pageNumber = Integer.valueOf(i)
        title = String.format(
            "%s %s / %s",
            pdfFileName,
            Integer.valueOf(i + 1),
            Integer.valueOf(i2)
        )
    }

    // com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
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
        fun start(context: Context, clientDTO: ClientDTO?) {
            val intent = Intent(context, ClientStatementPreviewActivity::class.java)
            intent.putExtra(MyConstants.CLIENT_DTO, clientDTO)
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
                    .setPositiveButton("ok") { dialogInterface, i ->
                        // from class: com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.ClientStatementPreviewActivity.2
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                            101
                        )
                    }
                    .setNegativeButton("no") { dialogInterface, i ->
                        // from class: com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.ClientStatementPreviewActivity.1
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