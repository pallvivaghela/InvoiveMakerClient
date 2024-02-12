package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView

class PhotoUploadActivity : AppCompatActivity(), View.OnClickListener {
    var filePath: String? = ""
    private var uploadedImage: ImageView? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_photo_upload)
        initLayout()
    }

    private fun initLayout() {
        findViewById<View>(R.id.choose_logo_text).setOnClickListener(this)
        findViewById<View>(R.id.delete_logo_text).setOnClickListener(this)
        uploadedImage = findViewById<View>(R.id.uploaded_image) as ImageView
    }

    override fun onClick(view: View) {
        if (view.id == R.id.choose_logo_text) {
            choosePhoto()
        }
    }

    private fun choosePhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = "android.intent.action.GET_CONTENT"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }


    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        super.onActivityResult(i, i2, intent)
        if (i == 1 && i2 == -1) {
            val path = getPath(intent!!.data)
            filePath = path
            if (path != null) {
                println(filePath)
            } else {
                println("selectedImagePath is null")
            }
            if (filePath != null) {
                println("selectedImagePath is the right one for you!")
            } else {
                println("filemanagerstring is the right one for you!")
            }
        }
    }

    fun getPath(uri: Uri?): String? {
        val managedQuery = managedQuery(uri, arrayOf("_data"), null, null, null)
            ?: return null
        val columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data")
        managedQuery.moveToFirst()
        return managedQuery.getString(columnIndexOrThrow)
    }
}