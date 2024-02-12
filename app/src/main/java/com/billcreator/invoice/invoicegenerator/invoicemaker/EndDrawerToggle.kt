package com.billcreator.invoice.invoicegenerator.invoicemaker

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener

class EndDrawerToggle(
    private val drawerLayout: DrawerLayout,
    toolbar: Toolbar,
    private val openDrawerContentDescRes: Int,
    private val closeDrawerContentDescRes: Int
) : DrawerListener {
    private var arrowDrawable: DrawerArrowDrawable? = null
    private val toggleButton: AppCompatImageButton


    override fun onDrawerStateChanged(i: Int) {}

    /* renamed from: lambda$new$0$com-billcreator-invoicemanager-invoicegenerator-invoicemaker-EndDrawerToggle  reason: not valid java name */
    fun m32x8861f2d(view: View?) {
        toggle()
    }

    fun syncState() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            setPosition(1.0f)
        } else {
            setPosition(0.0f)
        }
    }

    fun onConfigurationChanged(configuration: Configuration?) {
        loadDrawerArrowDrawable()
        syncState()
    }


    override fun onDrawerSlide(view: View, f: Float) {
        setPosition(Math.min(1.0f, Math.max(0.0f, f)))
    }


    override fun onDrawerOpened(view: View) {
        setPosition(1.0f)
    }


    override fun onDrawerClosed(view: View) {
        setPosition(0.0f)
    }

    @SuppressLint("WrongConstant")
    private fun loadDrawerArrowDrawable() {
        val drawerArrowDrawable = DrawerArrowDrawable(toggleButton.context)
        arrowDrawable = drawerArrowDrawable
        drawerArrowDrawable.direction = 3
        toggleButton.setImageDrawable(arrowDrawable)
    }

    private fun toggle() {
        val drawerLockMode = drawerLayout.getDrawerLockMode(GravityCompat.END)
        if (drawerLayout.isDrawerVisible(GravityCompat.END) && drawerLockMode != 2) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else if (drawerLockMode != 1) {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setPosition(f: Float) {
        if (f == 1.0f) {
            arrowDrawable!!.setVerticalMirror(true)
            setContentDescription(closeDrawerContentDescRes)
        } else if (f == 0.0f) {
            arrowDrawable!!.setVerticalMirror(false)
            setContentDescription(openDrawerContentDescRes)
        }
        arrowDrawable!!.progress = f
    }

    private fun setContentDescription(i: Int) {
        val appCompatImageButton = toggleButton
        appCompatImageButton.contentDescription = appCompatImageButton.context.getText(i)
    }

    init {
        val appCompatImageButton =
            AppCompatImageButton(toolbar.context, null, R.attr.toolbarNavigationButtonStyle)
        toggleButton = appCompatImageButton
        toolbar.addView(
            appCompatImageButton, Toolbar.LayoutParams(
                GravityCompat.END
            )
        )
        appCompatImageButton.setOnClickListener(`EndDrawerToggle$$ExternalSyntheticLambda0`(this))
        loadDrawerArrowDrawable()
    }
}