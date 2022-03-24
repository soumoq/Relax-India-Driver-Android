package org.relaxindia.driver.view.activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.sheet_notification_details.*
import org.json.JSONArray
import org.json.JSONObject
import org.relaxindia.driver.R
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.view.adapter.OtherServiceAdapter
import java.lang.Exception

class NotificationDetailsActivity : AppCompatActivity() {

    private var details = ""
    private var userName = ""
    private var userEmail = ""
    private var userPhone = ""
    private var createdAt = ""
    private var isReached = ""
    private var activity = ""
    private var bookingId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)

        booking_info_back.setOnClickListener {
            onBackPressed()
        }

        details = intent.getStringExtra("noti_details").toString()
        userName = intent.getStringExtra("user_name").toString()
        userEmail = intent.getStringExtra("user_email").toString()
        userPhone = intent.getStringExtra("user_phone").toString()
        createdAt = intent.getStringExtra("created_at").toString()
        isReached = intent.getStringExtra("is_reached").toString()
        activity = intent.getStringExtra("activity").toString()
        bookingId = intent.getStringExtra("booking_id").toString()
        //toast(isReached)

        if (activity.equals("DashboardActivity")) {
            if (isReached.equals("0"))
                i_am_reach.visibility = View.VISIBLE
            else
                i_am_reach.visibility = View.GONE

        } else {
            i_am_reach.visibility = View.GONE
        }

        if (activity.equals("NotificationActivity")) {
            noti_action.visibility = View.VISIBLE
        } else {
            noti_action.visibility = View.GONE
        }

        if (activity.equals("RejectedNotificationActivity")) {
            i_am_reach.visibility = View.GONE
            noti_action.visibility = View.GONE

        }


        booking_info_driver_name.text = userName
        booking_info_phone.text = userPhone
        booking_info_date.text = createdAt

        val jsonObj = JSONObject(details)
        booking_info_booking_amt.text = jsonObj.getString("booking_amount")
        booking_info_total_amt.text = jsonObj.getString("total_amount")
        booking_info_driver_amt.text =
            (jsonObj.getDouble("total_amount") - jsonObj.getDouble("total_amount")).toString()

        try {
            val serviceDeatils = jsonObj.getString("service_deatils")
            val detailsArr = JSONArray(serviceDeatils)
            Log.e("JSONOBJJJ", details)

            booking_info_from_loc.text = jsonObj.getString("from_location")
            booking_info_des_loc.text = jsonObj.getString("to_location")

            val otherServiceAdapter = OtherServiceAdapter(this)
            other_service_list.adapter = otherServiceAdapter
            otherServiceAdapter.updateData(detailsArr)


        } catch (e: Exception) {
            Log.e("JSONOBJJJ", e.message.toString())
        }

        noti_details_accept.setOnClickListener {
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

        noti_details_reject.setOnClickListener {
            VollyApi.rejectBooking(this, bookingId)
        }

        i_am_reach.setOnClickListener {
            VollyApi.reachedLoc(this, bookingId)
        }

        track_loc.setOnClickListener {
            val strUri =
                "http://maps.google.com/maps?q=loc:" + jsonObj.getString("user_latitude") + "," + jsonObj.getString(
                    "user_longitude"
                ) + " (" + "Label which you want" + ")"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))
            intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
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