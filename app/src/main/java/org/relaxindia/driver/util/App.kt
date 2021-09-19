package org.relaxindia.driver.util

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import org.relaxindia.driver.model.NotificationDataModel

object App {

    const val rs = "₹"

    //Notificatoin msg receive
    var notifyMsg : NotificationDataModel ?= null
    //Notificatoin msg send
    const val serverKey =
        "key=" + "AAAA1CWxbXI:APA91bGbT-na_V9dGiYNbIHUY7xj2g7GEJaZV3yCYoaqqIkVGzzutKBDWCjt5QeEAGF4tv5WaqcNB3KXrJ4rxGzXg8iMpdKAc5Q1pfHTWlNe4JV9JWRqndlw7FpE1tB-Dkn0tyEFuLLv"
    const val contentType = "application/json"
    const val FCM_API = "https://fcm.googleapis.com/fcm/send"

    const val agree =
        "I certify that the information provided is true & correct and I also agree the </font>and <font color=#1b9ff1>Terms & Condition</font>"

    //API String
    const val apiBaseUrl = "http://itmartsolution.com/demo/relaxindia.org/api/v1/driver/"
    const val apiLogin = "login"
    const val register = "register"
    const val verifyOtp = "verify-otp"
    const val profile = "profile"
    const val UPDATE_BOOKING = "update-booking"
    const val UPDATE_DEVICE_TOKEN = "update-device-token"


    //Share preference key
    const val preferenceUserToken = "user_token"
    const val preferenceUserPhone = "user_phone"
    const val preferenceUserEmail = "user_email"
    const val preferenceUserName = "user_name"
    const val preferenceUserId = "user_id"

    fun getUserID(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserId, "")!!
    }

    fun getUserPhone(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserPhone, "")!!
    }

    fun getUserEmail(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserEmail, "")!!
    }

    fun getUserName(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserName, "")!!
    }

    fun getUserToken(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return "Bearer ${(sp.getString(App.preferenceUserToken, "").toString())}"
    }

    fun openDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

        })

        val dialog = builder.create()
        dialog.show()
    }



    fun sendNotification(
        context: Context,
        array: ArrayList<String>,
        notificationData: NotificationDataModel
    ) {

        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(context)
        }

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        notifcationBody.put("title", "New Request")
        notifcationBody.put(
            "message",
            "A new patient found. Please accept or reject to click hare."
        )
        // notification message
        notifcationBody.put("booking_id", notificationData.bookingId)
        notifcationBody.put("source_loc", notificationData.sourceLoc)
        notifcationBody.put("des_loc", notificationData.desLoc)
        notifcationBody.put("amount", notificationData.amount)
        notifcationBody.put("device_id", notificationData.deviceId)


        notification.put("registration_ids", JSONArray(array))
        notification.put("data", notifcationBody)

        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
                //msg.setText("")
            },
            Response.ErrorListener {
                context.toast("Request error")
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

}