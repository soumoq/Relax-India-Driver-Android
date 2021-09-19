package org.relaxindia.driver.service.volly

import android.content.Context

import org.json.JSONException

import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import androidx.core.content.ContextCompat
import com.android.volley.*

import org.json.JSONObject

import com.android.volley.toolbox.StringRequest

import com.android.volley.toolbox.Volley
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.view.activity.DashboardActivity


object VollyApi {
    //UserAppBaseUrl
    private const val BASE_URL_USER = "http://itmartsolution.com/demo/relaxindia.org/api/v1/user/"
    private const val UPDATE_BOOKING = "update-booking"

    fun updateBooking(context: Context, orderId: String, deviceId : String) {
        val URL = "${App.apiBaseUrl}$UPDATE_BOOKING"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            App.notifyMsg = null
                            App.openDialog(context, "Booking update successfully.", "")
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", response)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: " + error)
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["booking_id"] = orderId
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    //Update device token
    fun updateDeviceToken(context: Context, deviceToken: String) {

        val URL = "${App.apiBaseUrl}${App.UPDATE_DEVICE_TOKEN}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            App.notifyMsg = null
                            //context.toast("Device token update successful")
                            if (deviceToken == "")
                                (context as DashboardActivity).logout()
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", response)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Device id not updated: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["device_token"] = deviceToken
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

}