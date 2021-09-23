package org.relaxindia.driver.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.viewModel.ApiCallViewModel

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profile_name.setText(App.getUserName(this))
        profile_mobile.setText(App.getUserPhone(this))
        profile_email.setText(App.getUserEmail(this))


        update_profile.setOnClickListener {
            if (profile_name.text!!.isNotEmpty() &&
                profile_mobile.text!!.isNotEmpty()
            ) {
                VollyApi.updateProfile(
                    this,
                    profile_name.text.toString(),
                    profile_email.text.toString(),
                    profile_mobile.text.toString()
                )
            } else {
                toast("Invalid input...")
            }
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