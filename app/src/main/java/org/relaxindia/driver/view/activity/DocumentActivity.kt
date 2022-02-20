package org.relaxindia.driver.view.activity

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
import android.content.DialogInterface
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import org.relaxindia.driver.util.loadImage


class DocumentActivity : AppCompatActivity() {

    private val SELECT_PHOTO = 1
    private val bitmaps = ArrayList<Bitmap>()
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        VollyApi.getUploadDocument(this)

        document_image_select.setOnClickListener {
            imageSelect()
        }

        document_image_view.setOnClickListener {
            VollyApi.getUploadDocument(this, "image")
        }

    }


    private fun imageSelect() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, SELECT_PHOTO)
    }

    fun getDocumentRes(getDocument: GetDocument, viewStatus: String) {
        toast("${getDocument.image} $viewStatus")
        if (getDocument.image.equals("null")) {
            document_image_view.visibility = View.GONE
        } else {
            document_image_view.visibility = View.VISIBLE
            if (viewStatus.equals("image")) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putExtra("view_status", getDocument.image)
                startActivity(intent)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
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
            saveImage(bitmaps, "image")

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close()
        }
    }

    private fun saveImage(myBitmap: ArrayList<Bitmap>, image: String) {
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
                    File(imageDirectory, "$image.jpg")
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
                    "image", imageFile[i].name,
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