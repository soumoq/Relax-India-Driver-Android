package org.relaxindia.driver

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.terms_service
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    //calender
    val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val text =
            "I certify that the information provided is true & correct and I also agree the </font>and <font color=#1b9ff1>Terms & Condition</font>"
        terms_service.text = Html.fromHtml(text)

        do_register.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        register_back.setOnClickListener {
            onBackPressed()
        }

        profile_image.setOnClickListener {
            pickImage()
        }


        val expiryDate =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                expiryDate()
            }

        val issueDate = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            issueDate()
        }

        expiry_date.setOnClickListener {
            val datePicker = DatePickerDialog(
                this, expiryDate, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )

            datePicker.datePicker.minDate = System.currentTimeMillis() + (1000 * 3600 * 24)
            datePicker.show()
        }

        issue_date.setOnClickListener {
            val datePicker = DatePickerDialog(
                this, issueDate, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )

            datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

    }

    private fun expiryDate() {
        val myFormat = "dd-MM-yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        expiry_date.setText(sdf.format(myCalendar.time))
    }

    private fun issueDate() {
        val myFormat = "dd-MM-yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        issue_date.setText(sdf.format(myCalendar.time))
    }

    private fun pickImage() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // ******** code for crop image
        // ******** code for crop image
        i.putExtra("crop", "true")
        i.putExtra("aspectX", 100)
        i.putExtra("aspectY", 100)
        i.putExtra("outputX", 256)
        i.putExtra("outputY", 356)

        try {
            i.putExtra("return-data", true)
            startActivityForResult(
                Intent.createChooser(i, "Select Picture"), 0
            )
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            try {
                val bundle = data?.extras
                val bitmap = bundle!!.getParcelable<Bitmap>("data")
//                val imageUrl = ImpFun.getImageUri(this,bitmap!!).toString()
                profile_image.setImageBitmap(bitmap)
//                view?.user_profile_image_select!!.loadImage(imageUrl)


            } catch (e: Exception) {
                e.printStackTrace()
//                context!!.toast(e.message.toString())
            }
        } else {
//            context!!.toast("Image selector failed")
            Toast.makeText(this, "Image selector failed", Toast.LENGTH_SHORT).show()

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