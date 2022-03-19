package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_rejected_notification.*
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.view.adapter.NotificationAdapter

class RejectedNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rejected_notification)

        rej_notification_back.setOnClickListener {
            onBackPressed()
        }


        VollyApi.getNotification(this, "rejected_notifications", "RejectedNotificationActivity")
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun setRejNotiList(notiList: ArrayList<NotificationApiModel>) {
        val notificationAdapter = NotificationAdapter(this, true)
        rej_notification_list.adapter = notificationAdapter
        notificationAdapter.updateData(notiList)
    }

    fun setRejNotiView(status: Boolean) {
        if (status) {
            rej_notification_layout.visibility = View.GONE
        } else {
            rej_notification_layout.visibility = View.VISIBLE
        }
    }

}