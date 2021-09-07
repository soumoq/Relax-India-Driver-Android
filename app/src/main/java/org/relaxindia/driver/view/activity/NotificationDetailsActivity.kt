package org.relaxindia.driver.view.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.sheet_notification_details.*
import org.relaxindia.driver.R

class NotificationDetailsActivity : AppCompatActivity() {

    //Bottom sheet
    lateinit var notificationDetailsSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)

        notificationDetailsSheet = BottomSheetDialog(this)
        notificationDetailsSheet.setContentView(R.layout.sheet_notification_details)
        notificationDetailsSheet.show()

        open_bottom_sheet.setOnClickListener {
            notificationDetailsSheet.show()
        }

        notificationDetailsSheet.before_accept.visibility = View.VISIBLE
        notificationDetailsSheet.after_accept.visibility = View.GONE

        notificationDetailsSheet.order_accept.setOnClickListener {
            notificationDetailsSheet.before_accept.visibility = View.GONE
            notificationDetailsSheet.after_accept.visibility = View.VISIBLE
        }

        notificationDetailsSheet.confirm_pickup.setOnClickListener {
            // setup the alert builder
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure?")

            // add button
            builder.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                startActivity(Intent(this, ThankYouActivity::class.java))
            })
            builder.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                notificationDetailsSheet.confirm_pickup.isChecked = false
            })
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }


    }
}