package org.relaxindia.driver.view.activity

import android.R.attr
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
import android.app.Activity
import java.io.FileNotFoundException
import android.provider.MediaStore.MediaColumns
import android.R.attr.data
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import org.relaxindia.driver.util.App
import java.io.IOException
import java.lang.Exception
import okhttp3.Route





class DocumentActivity : AppCompatActivity() {

    val image = 1
    val drivingLicence = 2
    val idProve = 3;
    val ambulancePaper = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        VollyApi.getUploadDocument(this)

        document_image_select.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, image)
        }

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
        if (requestCode > 0) if (resultCode === RESULT_OK) {
            val selectedImage: Uri = intentData?.data!!
            val filePath = getPath(selectedImage)
            val file_extn = filePath!!.substring(filePath!!.lastIndexOf(".") + 1)
            //image_name_tv.setText(filePath)
            toast("$requestCode/t" + filePath)
            try {
                if (file_extn == "img" || file_extn == "jpg" || file_extn == "jpeg" || file_extn == "gif" || file_extn == "png") {
                    //FINE
                    if (requestCode == 1)
                        uploadImage("image", filePath)
                } else {
                    //NOT IN REQUIRED FORMAT
                    toast("NOT IN REQUIRED FORMAT")
                }
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                toast("Exception : ${e.message}")
            }
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaColumns.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
            .getColumnIndexOrThrow(MediaColumns.DATA)
        cursor.moveToFirst()
        val imagePath = cursor.getString(column_index)
        return cursor.getString(column_index)
    }

    private fun uploadImage(param: String, filePath: String) {
        //Upload Images
        val client = OkHttpClient()
        try {
            val formBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            formBuilder.addFormDataPart(
                "Authorization",App.getUserToken(this)
            )
            formBuilder.addFormDataPart(
                "driving_licence", "param.png",
                RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    filePath
                )
            )

            val requestBody: RequestBody = formBuilder.build()
            val request = Request.Builder()
                .url(App.apiBaseUrl + App.UPLOAD_DOCUMENT)
                .post(requestBody)
                .build()




            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("UPLOADIMAGE", "ERROR")
                    this@DocumentActivity.toast("Error")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    Log.e("UPLOADIMAGE", "asdasa $response");
                }
            })

        } catch (e: Exception) {
            toast("Exception : ${e.message}")
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