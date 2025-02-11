package org.relaxindia.driver.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_register.*
import org.relaxindia.driver.R
import org.relaxindia.driver.model.firebaseModel.DriverProfileFirebase
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.viewModel.ApiCallViewModel

class OtpActivity : AppCompatActivity() {

    private lateinit var apiCallViewModel: ApiCallViewModel
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()

        otp_back.setOnClickListener {
            onBackPressed()
        }

        val phoneNumber: String = intent.getStringExtra("phone_number").toString()
        val text = "Don't receive SMS?  <font color=#1b9ff1>Resend OTP</font>"
        resend_sms.text = Html.fromHtml(text)
        otp_phone_number_display.text = "Enter the 6-digit code send to \n$phoneNumber"


        otp_proceed.button.setOnClickListener {
            apiCallViewModel.verifyOtpInfo(
                this,
                intent.getStringExtra("phone_number")!!,
                pin_view.text.toString()
            )
        }

    }


    private fun observeViewModel() {
        apiCallViewModel.verifyOtp.observe(this, Observer {
            if (!it.error) {
                val sp = getSharedPreferences("user_info", MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString(App.preferenceUserToken, it.data.access_token)
                editor.putString(App.preferenceUserId, it.data.id.toString())
                editor.commit()

                val driverProfile = DriverProfileFirebase(
                    it.data.id,
                    "",
                    intent.getStringExtra("phone_number")!!,
                    "",
                    "",
                    false,
                    false
                )

                database = FirebaseDatabase.getInstance().reference.child("driver_data")
                //FirebaseNodeName.child("user").child(customId).set(key, value);
                database.child(it.data.id.toString()).setValue(driverProfile).addOnSuccessListener {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

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