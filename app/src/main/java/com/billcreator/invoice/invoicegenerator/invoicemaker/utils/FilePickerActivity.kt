package com.billcreator.invoice.invoicegenerator.invoicemaker.utils

import android.annotation.SuppressLint
import android.app.ListActivity
import android.content.Context
import android.widget.ArrayAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.os.Bundle
import android.os.Environment
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import java.io.File
import java.io.FilenameFilter
import java.util.*

class FilePickerActivity : ListActivity() {
    protected lateinit var acceptedFileExtensions: Array<String?>
    protected var mAdapter: FilePickerListAdapter? = null
    protected var mDirectory: File? = null
    protected var mFiles: ArrayList<File>? = null
    protected var mShowHiddenFiles = false

    inner class ExtensionFilenameFilter(private val mExtensions: Array<String?>) : FilenameFilter {
        override fun accept(file: File, str: String): Boolean {
            var strArr: Array<String?>? = null
            if (File(file, str).isDirectory || mExtensions.also {
                    strArr = it
                } == null || strArr!!.size <= 0) {
                return true
            }
            for (str2 in strArr!!) {
                if (str.endsWith(str2!!)) {
                    return true
                }
            }
            return false
        }
    }

    inner class FileComparator : Comparator<File> {
        override fun compare(file: File, file2: File): Int {
            if (file === file2) {
                return 0
            }
            if (file.isDirectory && file2.isFile) {
                return -1
            }
            return if (!file.isFile || !file2.isDirectory) {
                file.name.compareTo(file2.name, ignoreCase = true)
            } else 1
        }
    }

    inner class FilePickerListAdapter(context: Context?, private val mObjects: List<File>?) :
        ArrayAdapter<File?>(
            context!!, R.layout.file_picker_list_item, 16908308, mObjects!!
        ) {
        @SuppressLint("WrongConstant")
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            if (view == null) {
                view = (context.getSystemService("layout_inflater") as LayoutInflater).inflate(
                    R.layout.file_picker_list_item,
                    viewGroup,
                    false
                )
            }
            val file = mObjects!![i]
            val imageView = view!!.findViewById<View>(R.id.file_picker_image) as ImageView
            val textView = view.findViewById<View>(R.id.file_picker_text) as TextView
            textView.isSingleLine = true
            textView.text = file.name
            if (file.isFile) {
                imageView.setImageResource(R.drawable.file)
            } else {
                imageView.setImageResource(R.drawable.folder)
            }
            return view
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val inflate = (getSystemService("layout_inflater") as LayoutInflater).inflate(
            R.layout.file_picker_empty_view,
            null as ViewGroup?
        )
        (listView.parent as ViewGroup).addView(inflate)
        listView.emptyView = inflate
        if (File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/Invoice"
            ).exists()
        ) {
            if (File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/Invoice/backup"
                ).exists()
            ) {
                mDirectory = File("/sdcard/Invoice/backup/")
            } else {
                mDirectory = File("/sdcard/Invoice/")
            }
        } else {
            mDirectory = File(DEFAULT_INITIAL_DIRECTORY)
        }
        mFiles = ArrayList()
        val filePickerListAdapter = FilePickerListAdapter(this, mFiles)
        mAdapter = filePickerListAdapter
        listAdapter = filePickerListAdapter
        acceptedFileExtensions = arrayOfNulls(0)
        if (intent.hasExtra(EXTRA_FILE_PATH)) {
            mDirectory = File(intent.getStringExtra(EXTRA_FILE_PATH))
        }
        if (intent.hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            mShowHiddenFiles = intent.getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false)
        }
        if (intent.hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
            val stringArrayListExtra = intent.getStringArrayListExtra(
                EXTRA_ACCEPTED_FILE_EXTENSIONS
            )
            acceptedFileExtensions = stringArrayListExtra!!.toTypedArray()
        }
    }

    public override fun onResume() {
        refreshFilesList()
        super.onResume()
    }

    fun refreshFilesList() {
        mFiles!!.clear()
        val listFiles = mDirectory!!.listFiles(ExtensionFilenameFilter(acceptedFileExtensions))
        if (listFiles != null && listFiles.size > 0) {
            for (file in listFiles) {
                if (!file.isHidden || mShowHiddenFiles) {
                    mFiles!!.add(file)
                }
            }
            Collections.sort(mFiles, FileComparator())
        }
        mAdapter!!.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (mDirectory!!.parentFile != null) {
            mDirectory = mDirectory!!.parentFile
            refreshFilesList()
            return
        }
        super.onBackPressed()
    }

    public override fun onListItemClick(listView: ListView, view: View, i: Int, j: Long) {
        val file = listView.getItemAtPosition(i) as File
        if (file.isFile) {
            val intent = Intent()
            intent.putExtra(EXTRA_FILE_PATH, file.absolutePath)
            setResult(-1, intent)
            finish()
        } else {
            mDirectory = file
            refreshFilesList()
        }
        super.onListItemClick(listView, view, i, j)
    }

    companion object {
        private const val DEFAULT_INITIAL_DIRECTORY = "/sdcard/"
        const val EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions"
        const val EXTRA_FILE_PATH = "file_path"
        const val EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files"
    }
}