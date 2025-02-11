package org.relaxindia.driver.service.volly

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log

import org.json.JSONException

import com.android.volley.*

import org.json.JSONObject

import com.android.volley.toolbox.StringRequest

import com.android.volley.toolbox.Volley
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.model.GetDashboard
import org.relaxindia.driver.model.GetDocument
import org.relaxindia.driver.model.Reached
import org.relaxindia.driver.model.ScheduleBookingModel
import org.relaxindia.driver.model.driverProfile.ProfileRes
import org.relaxindia.driver.service.GpsTracker
import org.relaxindia.driver.service.retrofit.ApiCallService
import org.relaxindia.driver.service.retrofit.RestApiServiceBuilder
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.view.activity.*
import retrofit2.Call
import retrofit2.Callback


object VollyApi {
    private lateinit var progressDialog: ProgressDialog

    fun registerDriver(
        context: Context,
        name: String,
        email: String,
        phone: String,
        pass: String,
        cPass: String
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait a while...")
        progressDialog.show()

        val URL = "${App.apiBaseUrl}${App.REGISTER}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        progressDialog.dismiss()
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            context.toast("Profile Updated")
                            (context as RegisterActivity).registerSuccess()
                        } else {
                            val dataObj = jsonObj.getJSONObject("data")
                            val errorObj = dataObj.getJSONObject("errors")
                            val keys = errorObj.keys()
                            val errorString = StringBuffer()
                            keys.forEach {
                                errorString.append("\u2022" + errorObj.getJSONArray(it)[0].toString() + "\n")
                            }
                            App.openDialog(
                                context,
                                "Error",
                                errorString.toString()
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
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["name"] = name
                    params["email"] = email
                    params["phone"] = phone
                    params["password"] = pass
                    params["password_confirmation"] = cPass

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

    }


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

                            val intent = Intent(context, DashboardActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
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
                                ""
                            )
                        } else {
                            App.openDialog(
                                context,
                                "Cancellation failed.",
                                ""
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
                                if (viewStatus.equals("NotificationActivity")) {
                                    (context as NotificationActivity).setNotiView(true)
                                } else if (viewStatus.equals("RejectedNotificationActivity")) {
                                    (context as RejectedNotificationActivity).setRejNotiView(true)
                                }

                                for (i in 0 until notiArr.length()) {
                                    val obj = notiArr.getJSONObject(i)
                                    val userObj = obj.getJSONObject("user")
                                    notiList.add(
                                        NotificationApiModel(
                                            obj.getInt("booking_id"),
                                            obj.getString("details"),
                                            obj.getString("created_at"),
                                            userObj.getString("name"),
                                            userObj.getString("phone"),
                                            userObj.getString("email"),
                                            obj.getInt("is_reached"),
                                            viewStatus
                                        )
                                    )
                                    if (viewStatus.equals("NotificationActivity")) {
                                        (context as NotificationActivity).setNotiList(notiList)
                                    } else if (viewStatus.equals("RejectedNotificationActivity")) {
                                        (context as RejectedNotificationActivity).setRejNotiList(
                                            notiList
                                        )
                                    } else {
                                        (context as DashboardActivity).setNotiList(notiList)
                                    }
                                }
                            } else {
                                if (viewStatus.equals("NotificationActivity")) {
                                    (context as NotificationActivity).setNotiView(false)
                                } else if (viewStatus.equals("RejectedNotificationActivity")) {
                                    (context as RejectedNotificationActivity).setRejNotiView(false)
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
                            val dataObj = jsonObj.getJSONObject("data")
                            val errorObj = dataObj.getJSONObject("errors")
                            val keys = errorObj.keys()
                            val errorString = StringBuffer()
                            keys.forEach {
                                errorString.append("\u2022" + errorObj.getJSONArray(it)[0].toString() + "\n")
                            }
                            App.openDialog(
                                context,
                                "Error",
                                errorString.toString()
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


    //get push notification
    fun getUploadDocument(context: Context, viewStatus: String = "") {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait a while...")
        progressDialog.show()

        val URL = "${App.apiBaseUrl}${App.GET_UPLOAD_DOCUMENTS}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        progressDialog.dismiss()
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            val dataobj = jsonObj.getJSONObject("data")
                            val getDocument = GetDocument(
                                dataobj.getString("image"),
                                dataobj.getString("driving_licence"),
                                dataobj.getString("id_proof"),
                                dataobj.getString("ambulance_paper"),
                            )
                            (context as DocumentActivity).getDocumentRes(getDocument, viewStatus)
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
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

    }

    //get schedule booking
    fun getScheduleBooking(context: Context) {
        context.toast("Please wait...")
        val URL = "${App.apiBaseUrl}${App.GET_SCHEDULE_BOOKING}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (error) {
                            context.toast("Something went wrong!!!")
                        } else {
                            val jsonArr = jsonObj.getJSONArray("data")
                            if (jsonArr.length() > 0) {
                                val objList = ArrayList<ScheduleBookingModel>()
                                for (i in 0 until jsonArr.length()) {
                                    val obj = jsonArr.getJSONObject(i)
                                    objList.add(
                                        ScheduleBookingModel(
                                            obj.getInt("user_id"),
                                            obj.getString("user_name"),
                                            obj.getString("user_phone"),
                                            obj.getString("user_image"),
                                            obj.getString("from_location"),
                                            obj.getString("to_location"),
                                            obj.getString("schedule_date_time"),
                                            obj.getString("user_comment"),
                                            obj.getDouble("booking_amount"),
                                            obj.getDouble("total_amount"),
                                            obj.getString("status"),
                                            obj.getString("date"),
                                        )
                                    )
                                }
                                (context as DashboardActivity).getScheduleBookingList(objList)
                            } else {
                                context.toast("No Schedule booking List Found...")
                            }
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
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
    fun getDashBoardDetails(context: Context) {
        val URL = "${App.apiBaseUrl}${App.DASHBOARD}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        progressDialog.dismiss()
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            val dataobj = jsonObj.getJSONObject("data")
                            val getDashboard = GetDashboard(
                                dataobj.getString("total_earning"),
                                dataobj.getString("total_journey"),
                                dataobj.getString("avg_rating"),
                            )
                            (context as DashboardActivity).getDashboardRes(getDashboard)
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


    fun reachedLoc(
        context: Context,
        bookingId: String,
    ) {
        val restApiService = RestApiServiceBuilder().buildService(ApiCallService::class.java)
        val response: Call<Reached> = restApiService.reached(App.getUserToken(context), bookingId)
        response.enqueue(object : Callback<Reached> {
            override fun onResponse(call: Call<Reached>, response: retrofit2.Response<Reached>) {
                //context.toast(response.body().toString())
                val reached = response.body()
                if (response.body()?.error == true) {
                    App.openDialog(context, "Error", response.body()?.message.toString())
                } else {
                    context.toast("Record saved successfully.")
                    (context as NotificationDetailsActivity).onBackPressed()
                }
            }

            override fun onFailure(call: Call<Reached>, t: Throwable) {
                context.toast("Error ${t.message}")
                Log.e("SADAS", t.message.toString())
            }

        })


    }


}