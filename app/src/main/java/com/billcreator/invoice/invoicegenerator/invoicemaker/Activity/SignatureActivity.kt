package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.kyanogen.signatureview.SignatureView
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SettingsDTO
import android.widget.TextView
import android.text.TextUtils
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.SignedDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import android.content.Intent
import android.view.View
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.sql.Timestamp
import java.util.*

class SignatureActivity : AppCompatActivity(), View.OnClickListener {
    private var catalogId: Long = 0
    private var signatureUrl: String? = ""
    var signatureView: SignatureView? = null

    class DatePickerFragment : DialogFragment(), OnDateSetListener {

        @SuppressLint("UseRequireInsteadOfGet")
        override fun onCreateDialog(bundle: Bundle?): Dialog {
            val instance = Calendar.getInstance()
            return DatePickerDialog(activity!!, this, instance[1], instance[2], instance[5])
        }

        override fun onDateSet(datePicker: DatePicker, i: Int, i2: Int, i3: Int) {
            val instance = Calendar.getInstance()
            instance[i, i2] = i3
            signedDate = instance.timeInMillis.toString()
            val unused = signedDate
            signedDate1!!.text = activity?.let {
                MyConstants.formatDate(
                    it,
                    java.lang.Long.valueOf(signedDate).toLong(),
                    settingsDTO!!.dateFormat
                )
            }
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_signature)
        intentData
        initLayout()
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) != 0
        ) {
            ActivityCompat.requestPermissions(this, mPERMISSIONSSTORAGE, 1)
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
        }
        if (catalogId > 0) {
            updateLayout()
        }
    }

    // androidx.activity.ComponentActivity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback, androidx.fragment.app.FragmentActivity
    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        if (i != 1) {
            super.onRequestPermissionsResult(i, strArr, iArr)
        } else {
            val i2 = iArr[0]
        }
    }

    private val intentData: Unit
        private get() {
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
            signedDate = intent.getStringExtra(MyConstants.SIGNED_DATE)
            signatureUrl = intent.getStringExtra(MyConstants.SIGNED_URL)
            settingsDTO = SettingsDTO.settingsDTO
        }

    private fun initLayout() {
        signatureView = findViewById<View>(R.id.signature_view) as SignatureView
        signedDate1 = findViewById<View>(R.id.signed_date) as TextView
        if (TextUtils.isEmpty(signedDate)) {
            signedDate = Calendar.getInstance().timeInMillis.toString()
        }
        signedDate1!!.text = MyConstants.formatDate(
            this,
            signedDate!!.toLong(),
            settingsDTO!!.dateFormat
        )
        findViewById<View>(R.id.save_sign).setOnClickListener(this)
        findViewById<View>(R.id.clear_sign).setOnClickListener(this)
        findViewById<View>(R.id.delete_sign).setOnClickListener(this)
        findViewById<View>(R.id.cancel_sign).setOnClickListener(this)
        signedDate1!!.setOnClickListener(this)
    }

    private fun updateLayout() {
        if (!TextUtils.isEmpty(signatureUrl)) {
            try {
                signatureView!!.setBitmap(
                    BitmapFactory.decodeFile(signatureUrl).copy(Bitmap.Config.ARGB_8888, true)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.cancel_sign -> finish()
            R.id.clear_sign -> {
                clearSign()
                return
            }
            R.id.delete_sign -> {
                deleteSign()
                finish()
                return
            }
            R.id.save_sign -> {
                saveSign()
                finish()
                return
            }
            R.id.signed_date -> {}
            else -> return
        }
        updateDate()
    }

    private fun updateDate() {
        DatePickerFragment().show(
            supportFragmentManager, "signatureDatePicker"
        )
    }

    private fun saveSign() {
        if (catalogId == 0L) {
            catalogId = MyConstants.createNewInvoice()
        }
        try {
            val signatureBitmap = signatureView!!.signatureBitmap
            val str = signatureUrl
            if (str == null || str.length == 0) {
                val str2 = MyConstants.rootDirectory + File.separator + "signature"
                val file = File(str2)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val timestamp = Timestamp(System.currentTimeMillis())
                timestamp.time = signedDate!!.toLong()
                signatureUrl = str2 + File.separator + "Signature_" + timestamp + ".jpg"
            }
            val fileOutputStream = FileOutputStream(File(signatureUrl))
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 40, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val signedDTO = SignedDTO()
        signedDTO.catalogId = catalogId
        signedDTO.signedDate = signedDate.toString()
        signedDTO.signedUrl = signatureUrl.toString()
        LoadDatabase.instance?.updateInvoiceSignature(signedDTO)
    }

    private fun clearSign() {
        signatureView!!.clearCanvas()
    }

    private fun deleteSign() {
        if (!TextUtils.isEmpty(signatureUrl)) {
            val file = File(signatureUrl)
            if (file.exists()) {
                file.delete()
            }
            val signedDTO = SignedDTO()
            signedDTO.catalogId = catalogId
            LoadDatabase.instance?.updateInvoiceSignature(signedDTO)
        }
    }

    companion object {
        private val mPERMISSIONSSTORAGE = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        private var settingsDTO: SettingsDTO? = null
        private var signedDate: String? = ""
        private var signedDate1: TextView? = null
        @JvmStatic
        fun start(context: Context, j: Long, str: String?, str2: String?) {
            val intent = Intent(context, SignatureActivity::class.java)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
            intent.putExtra(MyConstants.SIGNED_DATE, str)
            intent.putExtra(MyConstants.SIGNED_URL, str2)
            context.startActivity(intent)
        }
    }
}