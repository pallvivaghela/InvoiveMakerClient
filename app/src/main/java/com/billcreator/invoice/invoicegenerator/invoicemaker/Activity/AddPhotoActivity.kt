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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ImageDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Listener.ConfirmListener
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp

class AddPhotoActivity() : AppCompatActivity(), View.OnClickListener {
    private var addPhotoLayout: LinearLayout? = null
    private var additionalDetails: EditText? = null
    private var catalogId: Long = 0
    private var deleteItem: ImageView? = null
    private var description: EditText? = null
    private var dialog: AlertDialog? = null
    private var imageDTO: ImageDTO? = null
    private var itemImage: ImageView? = null
    private var newPath = ""
    private var oldPath = ""
    private var toolbar: Toolbar? = null
    private var uri: Uri? = null


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_add_photo)
        intentData
        initLayout()
        if (imageDTO != null) {
            updateImageInfo()
        }
    }

    private fun updateImageInfo() {
        description!!.setText(imageDTO!!.description)
        additionalDetails!!.setText(imageDTO!!.additionalDetails)
        val imageUrl = imageDTO!!.imageUrl
        oldPath = imageUrl.toString()
        MyConstants.loadImage(this, imageUrl, itemImage)
    }

    private val intentData: Unit
        private get() {
            imageDTO = intent.getSerializableExtra(MyConstants.IMAGE_DTO) as ImageDTO?
            catalogId = intent.getLongExtra(MyConstants.CATALOG_DTO, 0)
        }

    private fun initLayout() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        this.toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Photo"
        val linearLayout = findViewById<View>(R.id.add_photo_layout) as LinearLayout
        addPhotoLayout = linearLayout
        linearLayout.setOnClickListener(this)
        description = findViewById<View>(R.id.description) as EditText
        additionalDetails = findViewById<View>(R.id.additional_details) as EditText
        itemImage = findViewById<View>(R.id.item_image) as ImageView
        val imageView = findViewById<View>(R.id.delete_item) as ImageView
        deleteItem = imageView
        imageView.setOnClickListener(this)
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
        if (id != R.id.add_photo_layout) {
            if (id == R.id.delete_item) {
                deleteImage()
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
        if (TextUtils.isEmpty(oldPath)) {
            finish()
        } else {
            MyConstants.showConfirmDialog(
                this,
                getString(R.string.remove_item),
                getString(R.string.want_to_remove_this_item),
                object : ConfirmListener {
                    override fun cancel() {}
                    override fun ok() {
                        imageDTO?.let { LoadDatabase.instance!!.deleteInvoiceImage(it) }
                        finish()
                    }
                })
        }
    }

    private fun showPhotoChooserDialog() {
        val inflate = layoutInflater.inflate(R.layout.photo_choose_options, null as ViewGroup?)
        inflate.findViewById<View>(R.id.take_photo)
            .setOnClickListener(`AddPhotoActivity$$ExternalSyntheticLambda1`.INSTANCE)
        inflate.findViewById<View>(R.id.browse_photo)
            .setOnClickListener(View.OnClickListener { view ->

                m24x24cc6ca2(view)
            })
        dialog = AlertDialog.Builder(this).setView(inflate).show()
    }

       fun m24x24cc6ca2(view: View?) {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 103)
    }

    override fun onPause() {
        super.onPause()
        val alertDialog = dialog
        alertDialog?.dismiss()
    }

    override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        super.onActivityResult(i, i2, intent)
        if ((i == 103) && (i2 == -1) && (intent != null)) {
            val data = intent.data
            uri = data
            MyConstants.loadImage(this, data, itemImage)
            description!!.setText(MyConstants.getFileName(this, uri))
        }
    }

    private fun updateOnBackPressed() {
        if (uri != null) {
            try {
                val timestamp = Timestamp(System.currentTimeMillis())
                val rootDirectory = MyConstants.rootDirectory
                newPath = rootDirectory + File.separator + timestamp + ".jpg"
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val fileOutputStream = FileOutputStream(File(newPath))
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } catch (e2: Exception) {
                    e2.printStackTrace()
                    if (catalogId == 0L) {
                        catalogId = MyConstants.createNewInvoice()
                    }
                    if (imageDTO == null) {
                        imageDTO = ImageDTO()
                    }
                    imageDTO!!.catalogId = catalogId
                    imageDTO!!.imageUrl = newPath
                    imageDTO!!.description = description!!.text.toString().trim { it <= ' ' }
                    imageDTO!!.additionalDetails =
                        additionalDetails!!.text.toString().trim { it <= ' ' }
                    if (imageDTO!!.id > 0) {
                        LoadDatabase.instance!!.saveInvoiceImage(imageDTO!!)
                    } else {
                        LoadDatabase.instance!!.updateInvoiceImage(oldPath, imageDTO!!)
                    }
                }
            } catch (e3: Exception) {
                e3.printStackTrace()
                if (catalogId == 0L) {
                    catalogId = MyConstants.createNewInvoice()
                }
                if (imageDTO == null) {
                    imageDTO = ImageDTO()
                }
                imageDTO!!.catalogId = catalogId
                imageDTO!!.imageUrl = newPath
                imageDTO!!.description = description!!.text.toString().trim { it <= ' ' }
                imageDTO!!.additionalDetails =
                    additionalDetails!!.text.toString().trim { it <= ' ' }
                if (imageDTO!!.id > 0) {
                    LoadDatabase.instance!!.updateInvoiceImage(oldPath, imageDTO!!)
                } else {
                    LoadDatabase.instance!!.saveInvoiceImage(imageDTO!!)
                }
            }
            if (catalogId == 0L) {
                catalogId = MyConstants.createNewInvoice()
            }
            if (imageDTO == null) {
                imageDTO = ImageDTO()
            }
            imageDTO!!.catalogId = catalogId
            imageDTO!!.imageUrl = newPath
            imageDTO!!.description = description!!.text.toString().trim { it <= ' ' }
            imageDTO!!.additionalDetails = additionalDetails!!.text.toString().trim { it <= ' ' }
            if (imageDTO!!.id > 0) {
                LoadDatabase.instance!!.updateInvoiceImage(oldPath, imageDTO!!)
            } else {
                LoadDatabase.instance!!.saveInvoiceImage(imageDTO!!)
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
        var TAG = "AddPhotoActivity"

        @JvmStatic
        fun  `lambda$showPhotoChooserDialog$0`(view: View?) {
        }

        @JvmStatic
        fun start(context: Context, imageDTO: ImageDTO?, j: Long) {
            val intent = Intent(context, AddPhotoActivity::class.java)
            intent.putExtra(MyConstants.IMAGE_DTO, imageDTO)
            intent.putExtra(MyConstants.CATALOG_DTO, j)
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
                    .setPositiveButton("ok", object : DialogInterface.OnClickListener {


                        override fun onClick(dialogInterface: DialogInterface, i: Int) {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                                101
                            )
                        }
                    }).setNegativeButton(R.string.no, object : DialogInterface.OnClickListener {

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