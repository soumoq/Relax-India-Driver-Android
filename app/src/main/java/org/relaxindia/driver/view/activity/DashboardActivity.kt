package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.relaxindia.driver.R

class DashboardActivity : AppCompatActivity() {

    //nav-header
    lateinit var navHeader: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

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

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}