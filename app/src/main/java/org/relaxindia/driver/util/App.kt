package org.relaxindia.driver.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object App {
    const val agree =
        "I certify that the information provided is true & correct and I also agree the </font>and <font color=#1b9ff1>Terms & Condition</font>"

    //API String
    const val apiBaseUrl = "http://itmartsolution.com/demo/relaxindia.org/api/v1/driver/"
    const val apiLogin = "login"
    const val register = "register"

    fun openDialog(context: Context, title: String, message: String) {
        //val intent = Intent(context, TripAnalyzeActivity::class.java)
        //intent.putExtra("fileName", fileName)
        //context.startActivity(intent)


        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

        })

        // create and show the alert dialog

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

}