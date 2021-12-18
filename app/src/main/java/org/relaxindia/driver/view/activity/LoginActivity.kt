package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import org.relaxindia.driver.R
import org.relaxindia.driver.util.App
import org.relaxindia.driver.viewModel.ApiCallViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var apiCallViewModel: ApiCallViewModel

    private val serverKey =
        "key=" + "AAAA1CWxbXI:APA91bGbT-na_V9dGiYNbIHUY7xj2g7GEJaZV3yCYoaqqIkVGzzutKBDWCjt5QeEAGF4tv5WaqcNB3KXrJ4rxGzXg8iMpdKAc5Q1pfHTWlNe4JV9JWRqndlw7FpE1tB-Dkn0tyEFuLLv"
    private val contentType = "application/json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_goto_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.e("FCM_TOKEN", it)
        }

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/relaxIndia")


        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()

        login_continue.button.setOnClickListener {
            apiCallViewModel.loginInfo(
                this,
                login_phone_number.text.toString(),
                login_password.text.toString()
            )
        }

    }

    private fun observeViewModel() {
        apiCallViewModel.login.observe(this, Observer {
            if (!it.error) {
                Log.e("USERTOKEN", it.data.access_token)
                val sp = getSharedPreferences("user_info", MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString(App.preferenceUserToken, it.data.access_token)
                editor.commit()

                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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