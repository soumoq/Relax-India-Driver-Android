package org.relaxindia.driver.view.activity

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_login.terms_service
import kotlinx.android.synthetic.main.activity_register.*
import org.relaxindia.driver.R
import org.relaxindia.driver.util.App
import org.relaxindia.driver.viewModel.ApiCallViewModel
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)

        register_back.setOnClickListener {
            onBackPressed()
        }

        do_register.setOnClickListener {
            if (register_name.text!!.isNotEmpty() &&
                register_mobile.text!!.isNotEmpty() &&
                register_email.text!!.isNotEmpty() &&
                register_pass.text!!.isNotEmpty() &&
                register_confirm_pass.text!!.isNotEmpty()
            ) {
                if (register_pass.text.toString() == register_confirm_pass.text.toString() ) {
                    apiCallViewModel.registerInfo(
                        this,
                        register_name.text.toString(),
                        register_email.text.toString(),
                        register_mobile.text.toString(),
                        register_pass.text.toString(),
                        register_confirm_pass.text.toString()
                    )
                    observeViewModel()
                } else {
                    App.openDialog(
                        this,
                        "Error",
                        "Password mismatch or password must be grater then 7 "
                    )
                }
            } else {
                App.openDialog(
                    this,
                    "Error",
                    "Field not be blank"
                )
            }
        }

    }

    private fun observeViewModel() {
        apiCallViewModel.register.observe(this, androidx.lifecycle.Observer {
            if (!it.error) {
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("phone_number", register_mobile.text.toString())
                startActivity(intent)
            } else {
                App.openDialog(this, "Error", it.message)
            }
        })
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