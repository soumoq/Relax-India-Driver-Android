package org.relaxindia.driver.util

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
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
import org.relaxindia.driver.service.GpsTracker

object App {

    const val rs = "₹"

    //Notificatoin msg receive
    var notifyMsg: NotificationDataModel? = null

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
    const val PROFILE = "profile"
    const val UPDATE_BOOKING = "update-booking"
    const val UPDATE_DEVICE_TOKEN = "update-device-token"
    const val GET_NOTIFICATION = "get-push-notifications"
    const val REJECT_BOOKING: String = "reject-request"



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

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }



    fun sendNotification(
        context: Context,
        array: ArrayList<String>
    ) {
        val gpsTracker = GpsTracker(context)

        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(context)
        }

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        notifcationBody.put("title", "Request accepted")
        notifcationBody.put(
            "message",
            "Driver accept your order. Driver os on the way."
        )
        // notification message
        notifcationBody.put("booking_id", gpsTracker.latitude.toString())
        notifcationBody.put("source_loc", gpsTracker.longitude.toString())


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