package org.relaxindia.driver

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object App {

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