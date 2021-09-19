package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_notifiaction.*
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.view.adapter.NotificationAdapter

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifiaction)

        notification_back.setOnClickListener {
            onBackPressed()
        }


        VollyApi.getNotification(this)

    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun setNotiList(notiList: ArrayList<NotificationApiModel>) {
        val notificationAdapter = NotificationAdapter(this)
        notification_list.adapter = notificationAdapter
        notificationAdapter.updateData(notiList)
    }

    fun bookOrder(bookingId: String) {
        VollyApi.updateBooking(this, bookingId, "NA")
    }

}