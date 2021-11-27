package org.relaxindia.driver.view.activity

import android.R.attr
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_document.*
import org.relaxindia.driver.R
import org.relaxindia.driver.model.GetDocument
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.toast
import android.widget.Toast
import android.os.FileUtils
import android.R.attr.data
import android.net.Uri
import android.util.Log


class DocumentActivity : AppCompatActivity() {

    private val FILE_SELECT_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        document_image_select.setOnClickListener {

        }

        VollyApi.getUploadDocument(this)

    }

    fun getDocumentRes(getDocument: GetDocument) {
        toast(getDocument.image)
        if (getDocument.image.equals("null")) {
            document_image_download.visibility = View.GONE
        }
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