package com.billcreator.invoice.invoicegenerator.invoicemaker.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat
import com.billcreator.invoice.invoicegenerator.invoicemaker.MainActivity
import com.billcreator.invoice.invoicegenerator.invoicemaker.R

class SplashActivity : AppCompatActivity() {
    var splashDisplayLength = PathInterpolatorCompat.MAX_NUM_POINTS
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_splash)
        gotonext()
    }
    private fun gotonext() {
        Handler().postDelayed(
            `SplashActivity$$ExternalSyntheticLambda0`(this),
            splashDisplayLength.toLong()
        )
    }
    fun m30x9fc95971() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}