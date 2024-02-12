package com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MyPrintDocumentAdapter(private val mActivity: Activity, private val path: String) :
    PrintDocumentAdapter() {

    @SuppressLint("WrongConstant")
    override fun onLayout(
        printAttributes: PrintAttributes,
        printAttributes2: PrintAttributes,
        cancellationSignal: CancellationSignal,
        layoutResultCallback: LayoutResultCallback,
        bundle: Bundle
    ) {
        if (cancellationSignal.isCanceled) {
            layoutResultCallback.onLayoutCancelled()
        }
        layoutResultCallback.onLayoutFinished(
            PrintDocumentInfo.Builder("Invoice").setContentType(0).build(), true
        )
    }

    override fun onWrite(
        pageRangeArr: Array<PageRange>,
        parcelFileDescriptor: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        writeResultCallback: WriteResultCallback
    ) {
        var fileOutputStream: FileOutputStream?
        var th: Throwable?
        var fileInputStream: FileInputStream?
        var e: Exception
        var e2: Exception?
        var th2: Throwable?
        try {
            fileInputStream = FileInputStream(File(path))
            try {
                fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
            } catch (e3: Exception) {
                e2 = e3
                fileOutputStream = null
                e = e2
                try {
                    e.printStackTrace()
                    fileInputStream.close()
                } catch (th3: Throwable) {
                    th = th3
                    try {
                        fileInputStream.close()
                    } catch (e4: Exception) {
                        e4.printStackTrace()
                    }
                    throw th
                }
            } catch (th4: Throwable) {
                th2 = th4
                fileOutputStream = null
                th = th2
                fileInputStream.close()
                throw th
            }
            try {
                val bArr = ByteArray(1024)
                while (true) {
                    val read = fileInputStream.read(bArr)
                    if (read > 0) {
                        fileOutputStream!!.write(bArr, 0, read)
                    } else {
                        writeResultCallback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                        try {
                            fileInputStream.close()
                            fileOutputStream!!.close()
                            return
                        } catch (e5: Exception) {
                            e5.printStackTrace()
                            return
                        }
                    }
                }
            } catch (e6: Exception) {
                e = e6
                e.printStackTrace()
                fileInputStream.close()
                fileOutputStream!!.close()
            }
        } catch (e7: Exception) {
            e2 = e7
            fileOutputStream = null
            fileInputStream = null
            e = e2
            e.printStackTrace()
            try {
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            try {
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } catch (th5: Throwable) {
            th2 = th5
            fileOutputStream = null
            fileInputStream = null
            th = th2
            try {
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            try {
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            try {
                throw th
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }
}