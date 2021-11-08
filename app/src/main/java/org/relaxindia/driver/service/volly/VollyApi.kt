package org.relaxindia.driver.service.volly

import android.app.ProgressDialog
import android.content.Context

import org.json.JSONException

import com.android.volley.*

import org.json.JSONObject

import com.android.volley.toolbox.StringRequest

import com.android.volley.toolbox.Volley
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.service.GpsTracker
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.view.activity.DashboardActivity
import org.relaxindia.driver.view.activity.NotificationActivity
import org.relaxindia.driver.view.activity.ProfileActivity


object VollyApi {
    //UserAppBaseUrl
    private const val BASE_URL_USER = "http://itmartsolution.com/demo/relaxindia.org/api/v1/user/"
    private lateinit var progressDialog: ProgressDialog


    fun updateBooking(context: Context, orderId: String, deviceId: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait a while...")
        progressDialog.show()

        val URL = "${App.apiBaseUrl}${App.UPDATE_BOOKING}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    progressDialog.dismiss()
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            App.notifyMsg = null
                            App.openDialog(
                                context,
                                "Booking update successfully.",
                                "Thanks for accept the booking. We will let the patent know that you accept the booking."
                            )
                            val deviceIdArr = ArrayList<String>()
                            deviceIdArr.add(deviceId)
                            App.sendNotification(context, deviceIdArr)
                        } else {
                            App.openDialog(
                                context,
                                "Booking update failed.",
                                "Someone already accept the booking."
                            )
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", response)
                    }
                },
                Response.ErrorListener { error ->
                    progressDialog.dismiss()
                    context.toast("Something went wrong: $error")
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
                    val gpsTracker = GpsTracker(context)
                    params["booking_id"] = orderId
                    params["driver_latitude"] = gpsTracker.latitude.toString()
                    params["driver_longitude"] = gpsTracker.longitude.toString()

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun rejectBooking(context: Context, orderId: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait a while...")
        progressDialog.show()

        val URL = "${App.apiBaseUrl}${App.REJECT_BOOKING}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    progressDialog.dismiss()
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            App.notifyMsg = null
                            App.openDialog(
                                context,
                                "Cancellation successfully.",
                                "Thanks for accept the booking. We will let the patent know that you accept the booking."
                            )
                        } else {
                            App.openDialog(
                                context,
                                "Cancellation failed.",
                                "Someone already accept the booking."
                            )
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", response)
                    }
                },
                Response.ErrorListener { error ->
                    progressDialog.dismiss()
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    header["Content-Type"] = "application/json"
                    header["Accept"] = "application/json"
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


    //get push notification
    fun getNotification(context: Context, status: String, viewStatus: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait a while...")
        progressDialog.show()

        val URL = "${App.apiBaseUrl}${App.GET_NOTIFICATION}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        progressDialog.dismiss()
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            val notiList = ArrayList<NotificationApiModel>()
                            val notiData = jsonObj.getJSONObject("data")
                            val notiArr = notiData.getJSONArray(status)
                            if (notiArr.length() > 0) {
                                for (i in 0 until notiArr.length()) {
                                    val obj = notiArr.getJSONObject(i)
                                    notiList.add(
                                        NotificationApiModel(
                                            obj.getString("details"),
                                            obj.getString("created_at")
                                        )
                                    )
                                    if (viewStatus.equals("NotificationActivity")) {
                                        (context as NotificationActivity).setNotiList(notiList)
                                    } else {
                                        (context as DashboardActivity).setNotiList(notiList)
                                    }
                                }

                            }
                        } else {
                            App.openDialog(
                                context,
                                "Error",
                                "Something went wrong"
                            )
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "No Data Found...", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    progressDialog.dismiss()
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

    }

    //get push notification
    fun updateProfile(context: Context, name: String, email: String, phone: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait a while...")
        progressDialog.show()

        val URL = "${App.apiBaseUrl}${App.PROFILE}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    try {
                        progressDialog.dismiss()
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            context.toast("Profile Updated")
                            (context as ProfileActivity).profileUpdated()
                        } else {
                            App.openDialog(
                                context,
                                "Error",
                                jsonObj.getString("message")
                            )
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", response)
                    }
                },
                Response.ErrorListener { error ->
                    progressDialog.dismiss()
                    context.toast("Something went wrong: $error")
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
                    params["name"] = name
                    params["email"] = email
                    params["phone"] = phone
                    params["address"] = "Test"

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

    }


}