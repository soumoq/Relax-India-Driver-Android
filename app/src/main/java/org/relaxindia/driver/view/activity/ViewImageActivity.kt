package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_view_image.*
import org.relaxindia.driver.R
import org.relaxindia.driver.util.loadImage
import org.relaxindia.driver.util.loadImageForDocument
import org.relaxindia.driver.util.toast

class ViewImageActivity : AppCompatActivity() {

    private var viewStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        viewStatus = intent.getStringExtra("view_status").toString()
        //toast(viewStatus)
        view_image.loadImageForDocument(viewStatus)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}