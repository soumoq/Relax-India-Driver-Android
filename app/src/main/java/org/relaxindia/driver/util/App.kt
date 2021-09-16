package org.relaxindia.driver.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object App {

    //Notificatoin msg
    var notifyMsg = ""

    const val agree =
        "I certify that the information provided is true & correct and I also agree the </font>and <font color=#1b9ff1>Terms & Condition</font>"

    //API String
    const val apiBaseUrl = "http://itmartsolution.com/demo/relaxindia.org/api/v1/driver/"
    const val apiLogin = "login"
    const val register = "register"
    const val verifyOtp = "verify-otp"
    const val profile = "profile"

    //Share preference key
    const val preferenceUserToken = "user_token"

    fun getUserToken(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return "Bearer ${(sp.getString(App.preferenceUserToken, "").toString())}"
    }

    fun openDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

        })

        val dialog = builder.create()
        dialog.show()
    }

}