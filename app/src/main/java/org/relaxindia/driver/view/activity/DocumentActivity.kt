package org.relaxindia.driver.view.activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import kotlinx.android.synthetic.main.activity_document.*
import org.relaxindia.driver.R
import org.relaxindia.driver.model.GetDocument
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.toast
import android.provider.MediaStore
import java.io.File


class DocumentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        document_image_select.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        VollyApi.getUploadDocument(this)

    }

    fun getDocumentRes(getDocument: GetDocument) {
        toast(getDocument.image)
        if (getDocument.image.equals("null")) {
            document_image_download.visibility = View.GONE
        } else {
            document_image_download.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile: Uri? = intentData?.data //The uri with the location of the file
            val file =
                File("${Environment.getExternalStorageDirectory().absolutePath}${selectedFile?.path}")
            toast(getPath(selectedFile))
        }
    }

    private fun getPath(uri: Uri?): String {
        var path: String? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)

        if (cursor == null) {
            path = uri.path
        } else {
            cursor.moveToFirst();
            val columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return if (path == null || path.isEmpty()) uri.path!! else path
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