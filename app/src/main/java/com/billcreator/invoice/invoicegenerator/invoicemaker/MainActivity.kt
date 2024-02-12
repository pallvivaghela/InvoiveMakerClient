package com.billcreator.invoice.invoicegenerator.invoicemaker

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.net.MailTo
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Activity.SettingsActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.DatabaseHelper
import com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.*
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.FilePickerActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.LockScreen
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.MyConstants
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.SessionManager.Companion.getInstance
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainActivity() : AppCompatActivity(), View.OnClickListener {
    lateinit var activityTitles: Array<String>
    var mDrawerLayout: DrawerLayout? = null
    var mHandler: Handler? = null
    var menuBtn: ImageView? = null
    var navigationView: NavigationView? = null
    val shouldLoadHomeFragOnBackPress = true
    var toolbar: Toolbar? = null
    private fun toggleFab() {}
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)
        initLayout()
        if (MyConstants.CATALOG_TYPE == 1) {
            navItemIndex = 1
            TAG_CURRENT = TAG_ESTIMATES
            loadHomeFragment()
        } else if (bundle == null) {
            navItemIndex = 0
            TAG_CURRENT = TAG_HOME
            loadHomeFragment()
        }
        if (!intent.getBooleanExtra("from_app", false)) {
            passwordCheck()
        }
    }

    @SuppressLint("WrongConstant")
    private fun passwordCheck() {
        if (getInstance(
                applicationContext
            )!!.passcode != -1
        ) {
            val intent = Intent(applicationContext, LockScreen::class.java)
            intent.addFlags(67141632)
            intent.putExtra(
                "AppPasscode", getInstance(
                    applicationContext
                )!!.passcode
            )
            intent.putExtra("ScreenType", 2)
            startActivity(intent)
        }
    }

    private fun initLayout() {
        val toolbar2 = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar = toolbar2
        setSupportActionBar(toolbar2)
        mHandler = Handler()
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        activityTitles = resources.getStringArray(R.array.nav_item_activity_titles)
        menuBtn = findViewById<View>(R.id.ic_menu) as ImageView
        val navigationView2 = findViewById<View>(R.id.navigation_view) as NavigationView
        navigationView = navigationView2
        navigationView2.id = R.id.nav_invoice
        navigationView!!.setNavigationItemSelectedListener(
            `MainActivity$$ExternalSyntheticLambda1`(
                this
            )
        )
        menuBtn!!.setOnClickListener(`MainActivity$$ExternalSyntheticLambda0`(this))
    }

    /* renamed from: lambda$initLayout$0$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-MainActivity  reason: not valid java name */
    fun m33xdded6c0f(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        if (itemId == R.id.moreapp) {
            moreApp()
        } else if (itemId != R.id.rateus) {
            when (itemId) {
                R.id.nav_client -> {
                    navItemIndex = 4
                    TAG_CURRENT = TAG_CLIENTS
                }
                R.id.nav_estimate -> {
                    navItemIndex = 1
                    TAG_CURRENT = TAG_ESTIMATES
                }
                R.id.nav_exit -> finishAffinity()
                R.id.nav_feedback -> feedback()
                else -> when (itemId) {
                    R.id.nav_item -> {
                        navItemIndex = 3
                        TAG_CURRENT = TAG_MY_ITEMS
                    }
                    R.id.nav_policy -> privacyPolicy()
                    R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                    R.id.nav_share -> shareApp()
                    else -> {
                        navItemIndex = 0
                        TAG_CURRENT = TAG_HOME
                    }
                }
            }
        } else {
            rateUs()
        }
        startAnotherActivity()
        mDrawerLayout!!.closeDrawers()
        return true
    }

    /* renamed from: lambda$initLayout$1$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-MainActivity  reason: not valid java name */
    @SuppressLint("WrongConstant")
    fun m34x119b96d0(view: View?) {
        if (mDrawerLayout!!.isDrawerOpen(3)) {
            mDrawerLayout!!.closeDrawer(3)
        } else {
            mDrawerLayout!!.openDrawer(3)
        }
    }

    private fun privacyPolicy() {
        try {
            startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(getString(R.string.privacy_policy_link))
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun feedback() {
        try {
            val str = packageManager.getPackageInfo(packageName, 0).versionName
            val intent = Intent("android.intent.action.SENDTO")
            val sb = StringBuilder()
            sb.append(MailTo.MAILTO_SCHEME)
            sb.append(Uri.encode(getString(R.string.email_id)))
            sb.append("?subject=")
            sb.append(Uri.encode("App:" + getString(R.string.app_name)))
            sb.append("&body=")
            sb.append(Uri.encode("Give Your Valuable Feedback \n\n\n\nVersion : " + str + "\nAndroid OS : " + Build.VERSION.RELEASE + "\n Language: " + Locale.getDefault().displayLanguage + "\n TimeZone: " + TimeZone.getDefault().id))
            intent.data = Uri.parse(sb.toString().replace("\\n".toRegex(), "\n"))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareApp() {
        val decodeResource: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_banner)
        val file = File(externalCacheDir.toString() + "/banner.png")
        try {
            val fileOutputStream = FileOutputStream(file)
            decodeResource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val intent = Intent("android.intent.action.SEND")
            intent.type = "image/*"
            intent.putExtra(
                "android.intent.extra.TEXT",
                getString(R.string.app_name) + " \n\nDownload Free App:\n https://play.google.com/store/apps/details?id=" + packageName
            )
            val sb = StringBuilder()
            sb.append(packageName)
            sb.append(".provider")
            intent.putExtra(
                "android.intent.extra.STREAM",
                FileProvider.getUriForFile(this, sb.toString(), file)
            )
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */ /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x002c */
    @SuppressLint("WrongConstant")
    fun moreApp() {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("http://play.google.com/store/apps/dev?id=" + getString(R.string.developer_id))
        )
        intent.addFlags(469762048)
        startActivity(intent)
        try {
            startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("http://play.google.com/store/apps/dev?id=" + getString(R.string.developer_id))
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    fun rateUs() {
        try {
            val intent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("market://details?id=" + packageName)
            )
            intent.flags = 268435456
            startActivity(intent)
        } catch (unused: ActivityNotFoundException) {
            val intent2 = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
            )
            intent2.flags = 268435456
            startActivity(intent2)
        }
    }

    private fun setToolbarTitle() {
        supportActionBar!!.setTitle(activityTitles[navItemIndex])
    }

    private fun selectNavMenu() {
        navigationView!!.menu.getItem(navItemIndex).isChecked = true
    }

    val homeFragment: Fragment
        get() {
            val i = navItemIndex
            if (i == 0) {
                MyConstants.CATALOG_TYPE = 0
                Log.e("Nikhil", "getHomeFragment: ")
                return InvoiceFragment()
            } else if (i == 1) {
                MyConstants.CATALOG_TYPE = 1
                return EstimateFragment()
            } else if (i == 2) {
                return ReportFragment()
            } else {
                if (i == 3) {
                    return MyItemFragment()
                }
                return if (i != 4) {
                    InvoiceFragment()
                } else ClientFragment()
            }
        }

    @SuppressLint("ResourceType")
    private fun loadHomeFragment() {
        invalidateOptionsMenu()
        selectNavMenu()
        setToolbarTitle()
        if (supportFragmentManager.findFragmentByTag(TAG_CURRENT) != null) {
            mDrawerLayout!!.closeDrawers()
            toggleFab()
            return
        }
        mHandler!!.post {
            val homeFragment = homeFragment
            val beginTransaction = this@MainActivity.supportFragmentManager.beginTransaction()
            beginTransaction.setCustomAnimations(17432576, 17432577)
            beginTransaction.replace(R.id.content, homeFragment, TAG_CURRENT)
            beginTransaction.commitAllowingStateLoss()
        }
        toggleFab()
        mDrawerLayout!!.closeDrawers()
    }

    override fun onClick(view: View) {
        view.id
    }

    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout!!.closeDrawers()
            return
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage(getString(R.string.exit_confirmation)).setCancelable(false)
            .setPositiveButton(R.string.yes, object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    moveTaskToBack(true)
                    Process.killProcess(Process.myPid())
                    System.exit(1)
                }
            }).setNegativeButton(R.string.no, object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    dialogInterface.cancel()
                }
            })
        builder.create().show()
    }

    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        var data: Uri? = null
        val str: String
        val str2: String
        super.onActivityResult(i, i2, intent)
        if (i == 80 && i2 == -1) {
            importOperation(intent!!.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH))
        } else if ((i == 14) && (i2 == -1) && ((intent!!.data.also { data = (it)!! }) != null)) {
            val query = contentResolver.query(data!!, null, null, null, null)
            if (query == null) {
                str = data!!.path!!.substring(data!!.path!!.lastIndexOf("/") + 1)
                str2 = data!!.path!!.substring(data!!.path!!.lastIndexOf(".") + 1)
            } else {
                query.moveToFirst()
                val string = query.getString(2)
                val string2 = query.getString(4)
                query.close()
                str = string
                str2 = string2
            }
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Restore")
            builder.setMessage("Do you want to restore from file : $str")
            builder.setPositiveButton("Restore", object : DialogInterface.OnClickListener {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity.AnonymousClass4 */
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    if ((str2 == "application/x-sqlite3") || (str2 == "bak")) {
                        val mainActivity = this@MainActivity
                        val show: ProgressDialog = ProgressDialog.show(
                            mainActivity,
                            mainActivity.getString(R.string.app_name),
                            "Proccessing...",
                            true
                        )
                        Thread(object : Runnable {
                            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity.AnonymousClass4.AnonymousClass1 */
                            @SuppressLint("WrongConstant")
                            override fun run() {
                                try {
                                    restoreDB(
                                        this@MainActivity.contentResolver.openInputStream(
                                            (intent.data)!!
                                        )
                                    )
                                    (this@MainActivity.application as AppCore).init()
                                    val intent = Intent(
                                        this@MainActivity.applicationContext,
                                        MainActivity::class.java
                                    )
                                    intent.putExtra("from_app", true)
                                    intent.addFlags(67141632)
                                    this@MainActivity.startActivity(intent)
                                    show.dismiss()
                                    runOnUiThread(Runnable
                                    /* class com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity.AnonymousClass4.AnonymousClass1.AnonymousClass1 */
                                    {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "File is imported successfully",
                                            0
                                        ).show()
                                    })
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }).start()
                        return
                    }
                    showErrorDialog()
                }
            })
            builder.setNegativeButton(
                getString(R.string.cancel),
                object : DialogInterface.OnClickListener {
                    /* class com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity.AnonymousClass5 */
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {}
                })
            builder.create()
            builder.show()
        }
    }

    @SuppressLint("WrongConstant")
    fun importOperation(str: String?) {
        getString(R.string.app_name)
        java.lang.Boolean.valueOf(true)
        if ((getExtension(str) == "bak")) {
            UpdateDatabase().execute(str)
            return
        }
        Toast.makeText(this, "Wrong file is uploaded.", 0).show()
    }

    private fun getExtension(str: String?): String? {
        return if (str!!.contains(".")) {
            str.substring(str.lastIndexOf(46.toChar()) + 1)
        } else null
    }

    @Throws(IOException::class)
    private fun restoreDB(inputStream: InputStream?) {
        val fileOutputStream =
            FileOutputStream(getDatabasePath(DatabaseHelper.DATABASE_NAME).toString())
        while (true) {
            val read = inputStream!!.read()
            if (read != -1) {
                fileOutputStream.write(read)
            } else {
                inputStream.close()
                fileOutputStream.close()
                return
            }
        }
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle("Restore")
        builder.setMessage("You have chosen wrong file! Please select a valid file to restore.")
        builder.create()
        builder.show()
    }

    private fun startAnotherActivity() {
        loadHomeFragment()
    }

    inner class UpdateDatabase internal constructor() : AsyncTask<String?, Void?, Boolean?>() {
        var progDailog: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            val mainActivity = this@MainActivity
            progDailog = ProgressDialog.show(
                mainActivity,
                mainActivity.getString(R.string.app_name),
                "Proccessing...",
                true
            )
        }

        fun onPostExecute(bool: Boolean) {
            super.onPostExecute(bool)
            Handler().postDelayed(object : Runnable {
                /* class com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity.UpdateDatabase.AnonymousClass1 */
                @SuppressLint("WrongConstant")
                override fun run() {
                    if (bool) {
                        Toast.makeText(this@MainActivity, "File is imported successfully", 0).show()
                        (this@MainActivity.application as AppCore).init()
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra("from_app", true)
                        intent.addFlags(67141632)
                        this@MainActivity.startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "Something went wrong!", 0).show()
                    }
                    progDailog!!.dismiss()
                }
            }, 2000)
        }

        override fun doInBackground(vararg p0: String?): Boolean? {
            var z = false
            try {
                z = DatabaseHelper(this@MainActivity).copyDbOperation(
                    p0[0],
                    getDatabasePath(DatabaseHelper.DATABASE_NAME).toString()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return java.lang.Boolean.valueOf(z)
        }

    }

    companion object {
        val TAG_CLIENTS = "tag_clients"
        var TAG_CURRENT = "tag_home"
        val TAG_ESTIMATES = "tag_estimates"
        val TAG_HOME = "tag_home"
        val TAG_MY_ITEMS = "tag_my_items"
        val TAG_REPORTS = "tag_reports"
        val TAG_SETTINGS = "tag_settings"
        var navItemIndex = 0
    }
}