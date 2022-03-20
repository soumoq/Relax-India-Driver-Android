package org.relaxindia.driver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_notifiaction.*
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.view.adapter.NotificationAdapter
import java.lang.Exception

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifiaction)

        notification_back.setOnClickListener {
            onBackPressed()
        }


        VollyApi.getNotification(this, "pending_notifications", "NotificationActivity")

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
        try {
            val database = FirebaseDatabase.getInstance().reference.child("driver_data")
            val updateInfo = HashMap<String, Any>()
            updateInfo["online"] = false
            val userId: Int = App.getUserID(this).toInt()
            if (userId >= 0) {
                database.child(App.getUserID(this)).updateChildren(updateInfo)
            }
        } catch (e: Exception) {
            toast(e.message.toString())
        }


        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            VollyApi.updateBooking(this, bookingId, it)
        }


    }


    fun rejectBooking(id: String) {
        toast(id)
        VollyApi.rejectBooking(this, id)
    }

    fun setNotiView(status: Boolean) {
        if (status) {
            notification_layout.visibility = View.GONE
        } else {
            notification_layout.visibility = View.VISIBLE
        }
    }

}