package org.relaxindia.driver.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.util.Log
import okhttp3.*
import org.relaxindia.driver.util.App
import java.lang.Exception
import java.io.*
import kotlin.collections.ArrayList
import android.graphics.BitmapFactory


class DocumentActivity : AppCompatActivity() {

    private val SELECT_PHOTO = 1
    private val bitmaps = ArrayList<Bitmap>()
    lateinit var progressDialog: ProgressDialog

    private var imageKey = "image"
    private var drivingLicenceKey = "driving_licence"
    private var idProofKey = "id_proof"
    private var ambulancePaperKey = "ambulance_paper"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        VollyApi.getUploadDocument(this)

        document_image_select.setOnClickListener {
            imageSelect(1)
        }

        document_licence_select.setOnClickListener {
            imageSelect(2)
        }

        document_id_select.setOnClickListener {
            imageSelect(3)
        }

        document_ap_select.setOnClickListener {
            imageSelect(4)
        }

        document_image_view.setOnClickListener {
            VollyApi.getUploadDocument(this, imageKey)
        }

        document_licence_view.setOnClickListener {
            VollyApi.getUploadDocument(this, drivingLicenceKey)
        }

        document_id_view.setOnClickListener {
            VollyApi.getUploadDocument(this, idProofKey)
        }

        document_ap_view.setOnClickListener {
            VollyApi.getUploadDocument(this, ambulancePaperKey)
        }

    }


    fun getDocumentRes(getDocument: GetDocument, viewStatus: String) {
        //toast("${getDocument.image} ${getDocument.driving_licence} ${getDocument.id_proof} ${getDocument.ambulance_paper} $viewStatus")
        if (getDocument.image.equals("null")) {
            document_image_view.visibility = View.GONE
        } else {
            document_image_view.visibility = View.VISIBLE
            if (viewStatus.equals(imageKey)) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putExtra("view_status", getDocument.image)
                startActivity(intent)
            }
        }

        if (getDocument.driving_licence.equals("null")) {
            document_licence_view.visibility = View.GONE
        } else {
            document_licence_view.visibility = View.VISIBLE
            if (viewStatus.equals(drivingLicenceKey)) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putExtra("view_status", getDocument.driving_licence)
                startActivity(intent)
            }
        }

        if (getDocument.id_proof.equals("null")) {
            document_id_view.visibility = View.GONE
        } else {
            document_id_view.visibility = View.VISIBLE
            if (viewStatus.equals(idProofKey)) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putExtra("view_status", getDocument.id_proof)
                startActivity(intent)
            }
        }

        if (getDocument.ambulance_paper.equals("null")) {
            document_ap_view.visibility = View.GONE
        } else {
            document_ap_view.visibility = View.VISIBLE
            if (viewStatus.equals(ambulancePaperKey)) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putExtra("view_status", getDocument.ambulance_paper)
                startActivity(intent)
            }
        }
    }

    private fun imageSelect(viewStatusCode: Int) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, viewStatusCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode >= 1 && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            val pickedImage = data.data
            // Let's read picked image path using content resolver
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(pickedImage!!, filePath, null, null, null)
            cursor!!.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(imagePath, options)

            // Do something with the bitmap
            bitmaps.add(bitmap)
            var viewStatus = ""
            when (requestCode) {
                1 -> {
                    viewStatus = imageKey
                }
                2 -> {
                    viewStatus = drivingLicenceKey

                }
                3 -> {
                    viewStatus = idProofKey

                }
                4 -> {
                    viewStatus = ambulancePaperKey
                }
            }
            //toast(viewStatus)
            saveImage(bitmaps, viewStatus)

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close()
        }
    }

    private fun saveImage(myBitmap: ArrayList<Bitmap>, keyName: String) {
        val imagePath = ArrayList<String>()

        for (i in myBitmap.indices) {
            val bytes = ByteArrayOutputStream()
            myBitmap.get(i).compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            val imageDirectory =
                File(Environment.getExternalStorageDirectory().toString() + "/RelaxIndia/Documents")
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs()
            }
            try {
                val f =
                    File(imageDirectory, "$keyName.jpg")
                f.createNewFile()
                val fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
                fo.close()
                imagePath.add(f.absolutePath)
                Log.e("GET_PATH", f.absolutePath)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }

        //upload image
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait will send you a otp")
        progressDialog.show()

        val client = OkHttpClient()
        try {
            val imageFile = ArrayList<File>()
            for (i in imagePath.indices) {
                imageFile.add(File(imagePath[i]))
            }
            val formBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
//            formBuilder.addFormDataPart("userid", userId)
//            formBuilder.addFormDataPart("comment", comment.getText().toString())
            for (i in imageFile.indices) {
                formBuilder.addFormDataPart(
                    keyName, imageFile[i].name,
                    RequestBody.create(MediaType.parse("image/jpeg"), imageFile[i])
                )
            }
            val requestBody: RequestBody = formBuilder.build()
            val request = Request.Builder()
                .url("${App.apiBaseUrl}upload-documents")
                .addHeader("Authorization", App.getUserToken(this))
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    toast(e.message.toString())
                    progressDialog.dismiss()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    progressDialog.dismiss()
                    finish()
                    startActivity(intent)
                }
            })
        } catch (e: Exception) {
            toast(e.message.toString())
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