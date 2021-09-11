package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import org.relaxindia.driver.R
import org.relaxindia.driver.util.App
import org.relaxindia.driver.viewModel.ApiCallViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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