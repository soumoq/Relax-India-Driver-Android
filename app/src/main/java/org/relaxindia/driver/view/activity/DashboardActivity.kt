package org.relaxindia.driver.view.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.viewModel.ApiCallViewModel

class DashboardActivity : AppCompatActivity() {

    //nav-header
    lateinit var navHeader: View

    lateinit var apiCallViewModel: ApiCallViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        Log.e("DRIVER_TOKEN", App.getUserToken(this))
        apiCallViewModel.profileInfo(this)

        if (App.notifyMsg != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Request from patent")
            val message =
                "<b>Stating Address :</b> ${App.notifyMsg?.sourceLoc}<b><br>Destination Address :</b> ${App.notifyMsg?.desLoc}<br><b>Amount : </b>${App.notifyMsg?.amount}"
            builder.setMessage(Html.fromHtml(message))

            builder.setPositiveButton("Accept", DialogInterface.OnClickListener { dialog, which ->
                Log.e("ALL_RES","${App.getUserToken(this)}\n${App.notifyMsg?.bookingId!!}\n${App.getUserID(this)}")
                VollyApi.updateBooking(this@DashboardActivity,App.notifyMsg?.bookingId!!)
            })

            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, i ->

            })

            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }


        open_notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        navHeader = nav_view.getHeaderView(0)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {

                }
                R.id.menu_your_journey -> {

                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
                R.id.menu_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                }
            }
            true
        }

        dashboard_logout.setOnClickListener {
            val sp = applicationContext.getSharedPreferences("user_info", MODE_PRIVATE)
            val editor = sp.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


    }

    private fun observeViewModel() {
        apiCallViewModel.profileVar.observe(this, Observer {
            if (!it.error) {
                val sp = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString(App.preferenceUserId, it.data.id)
                editor.putString(App.preferenceUserPhone, it.data.phone)
                editor.putString(App.preferenceUserEmail, it.data.email)
                editor.putString(App.preferenceUserName, it.data.name)
                editor.commit()

                navHeader.nav_username.text = it.data.name
                navHeader.nav_phone.text = it.data.phone
                navHeader.nav_image.text = it.data.name.take(1)
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