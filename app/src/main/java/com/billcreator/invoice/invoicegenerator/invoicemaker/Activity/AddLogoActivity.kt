package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.BusinessDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp

class AddLogoActivity : AppCompatActivity(), View.OnClickListener {
    private var businessId: Long = 0
    private var chooseLogo: TextView? = null
    private var deleteLogo: TextView? = null
    private var dialog: AlertDialog? = null
    private var imgUrl: String? = ""
    private var ivDone: ImageView? = null
    private var logoImage: ImageView? = null
    private var toolbar: Toolbar? = null
    private var uri: Uri? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_add_logo)
        intentData
        initLayout()
        updateLayout()
    }

    private val intentData: Unit
        private get() {
            imgUrl = intent.getStringExtra(MyConstants.IMAGE_URL)
            businessId = intent.getLongExtra(MyConstants.BUSINESS_DTO, 0)
        }

    private fun initLayout() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        ivDone = findViewById<View>(R.id.iv_done) as ImageView
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.business_logo)
        logoImage = findViewById<View>(R.id.logo_image) as ImageView
        val textView = findViewById<View>(R.id.choose_logo) as TextView
        chooseLogo = textView
        textView.setOnClickListener(this)
        val textView2 = findViewById<View>(R.id.delete_logo) as TextView
        deleteLogo = textView2
        textView2.setOnClickListener(this)
        ivDone!!.setOnClickListener(this)
    }

    private fun updateLayout() {
        if (!TextUtils.isEmpty(imgUrl)) {
            MyConstants.loadImage(this, imgUrl, logoImage)
        }
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr)
        } else if (iArr.size != 0 && iArr[0] == 0) {
            showPhotoChooserDialog()
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id != R.id.choose_logo) {
            if (id == R.id.delete_logo) {
                deleteImage()
            }
            if (id == R.id.iv_done) {
                onBackPressed()
            }
        } else if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) == 0
        ) {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, 103)
        } else {
            requestPermission(this)
        }
    }

    private fun deleteImage() {
        if (!TextUtils.isEmpty(imgUrl)) {
            imgUrl = MyConstants.deleteFile(imgUrl)
            LoadDatabase.instance?.updateBusinessLogo(businessId, imgUrl)
            Glide.with((this as FragmentActivity)).load(
                Integer.valueOf(
                    resources.getIdentifier(
                        "ic_photo_camera",
                        "drawable",
                        packageName
                    )
                )
            ).into(
                logoImage!!
            )
        }
        uri = null
    }

    private fun showPhotoChooserDialog() {
        val inflate = layoutInflater.inflate(R.layout.photo_choose_options, null as ViewGroup?)
        inflate.findViewById<View>(R.id.browse_photo)
            .setOnClickListener(`AddLogoActivity$$ExternalSyntheticLambda0`(this))
        dialog = AlertDialog.Builder(this).setView(inflate).show()
    }

    fun m23xfb2d5b38(view: View?) {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 103)
    }

    public override fun onPause() {
        super.onPause()
        val alertDialog = dialog
        alertDialog?.dismiss()
    }

    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        super.onActivityResult(i, i2, intent)
        if (i == 103 && i2 == -1 && intent != null) {
            val data = intent.data
            uri = data
            MyConstants.loadImage(this, data, logoImage)
        }
    }

    private fun updateOnBackPressed() {
        if (uri != null) {
            val str =
                MyConstants.rootDirectory + File.separator + "business_logo_" + Timestamp(
                    System.currentTimeMillis()
                ) + ".jpg"
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val fileOutputStream = FileOutputStream(File(str))
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)
                    bitmap.recycle()
                    try {
                        fileOutputStream.close()
                        deleteImage()
                        if (businessId <= 0) {
                            businessId =
                                LoadDatabase.instance!!.saveBusinessInformation(BusinessDTO())
                        }
                        LoadDatabase.instance?.updateBusinessLogo(businessId, str)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } catch (e2: Exception) {
                    e2.printStackTrace()
                    deleteImage()
                    if (businessId <= 0) {
                        businessId =
                            LoadDatabase.instance!!.saveBusinessInformation(BusinessDTO())
                    }
                    LoadDatabase.instance?.updateBusinessLogo(businessId, str)
                } catch (th: Throwable) {
                    deleteImage()
                    if (businessId <= 0) {
                        businessId =
                            LoadDatabase.instance!!.saveBusinessInformation(BusinessDTO())
                    }
                    LoadDatabase.instance?.updateBusinessLogo(businessId, str)
                    try {
                        throw th
                    } catch (th2: Throwable) {
                        th2.printStackTrace()
                    }
                }
            } catch (e3: Exception) {
                e3.printStackTrace()
                deleteImage()
                if (businessId <= 0) {
                    businessId = LoadDatabase.instance!!.saveBusinessInformation(BusinessDTO())
                }
                LoadDatabase.instance!!.updateBusinessLogo(businessId, str)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateOnBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        var TAG = "AddLogoActivity"

        @JvmStatic
        fun start(context: Context, str: String?, j: Long) {
            val intent = Intent(context, AddLogoActivity::class.java)
            intent.putExtra(MyConstants.IMAGE_URL, str)
            intent.putExtra(MyConstants.BUSINESS_DTO, j)
            context.startActivity(intent)
        }

        private fun requestPermission(context: Context) {
            val activity: Activity = context as Activity
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                )
            ) {
                AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.permission_storage))
                    .setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface, i: Int) {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                                101
                            )
                        }
                    }).setNegativeButton(
                        activity.getString(R.string.no),
                        object : DialogInterface.OnClickListener {

                            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                                dialogInterface.dismiss()
                            }
                        }).show()
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