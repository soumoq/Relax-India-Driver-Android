package org.relaxindia.driver.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.relaxindia.driver.model.NotificationDataModel

object App {

    const val rs = "₹"

    //Notificatoin msg
    var notifyMsg : NotificationDataModel ?= null

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

}