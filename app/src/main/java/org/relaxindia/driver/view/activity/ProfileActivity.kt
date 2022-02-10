package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_profile.*
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        register_back.setOnClickListener {
            onBackPressed()
        }

        profile_name.setText(App.getUserName(this))
        profile_mobile.setText(App.getUserPhone(this))
        profile_email.setText(App.getUserEmail(this))


        update_profile.setOnClickListener {

            VollyApi.updateProfile(
                this,
                profile_name.text.toString(),
                profile_email.text.toString(),
                profile_mobile.text.toString()
            )

        }


    }

    fun profileUpdated() {
        onBackPressed()
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