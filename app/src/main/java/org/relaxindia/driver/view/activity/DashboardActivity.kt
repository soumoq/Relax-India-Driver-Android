package org.relaxindia.driver.view.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.sheet_booking_list.*
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.R
import org.relaxindia.driver.model.GetDashboard
import org.relaxindia.driver.model.ScheduleBookingModel
import org.relaxindia.driver.service.GpsTracker
import org.relaxindia.driver.service.updateLocation.LocUpdateService
import org.relaxindia.driver.service.volly.VollyApi
import org.relaxindia.driver.util.App
import org.relaxindia.driver.util.toast
import org.relaxindia.driver.view.adapter.NotificationAdapter
import org.relaxindia.driver.view.adapter.ScheduleBookingAdapter
import org.relaxindia.driver.viewModel.ApiCallViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardActivity : AppCompatActivity() {

    //nav-header
    private lateinit var navHeader: View

    private lateinit var apiCallViewModel: ApiCallViewModel

    private lateinit var database: DatabaseReference

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    //Bottom Sheet
    lateinit var bookingListSheet: BottomSheetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        startService(Intent(this, LocUpdateService::class.java))

        bookingListSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bookingListSheet.setContentView(R.layout.sheet_booking_list)
        bookingListSheet.back_sheet_schedule.setOnClickListener {
            bookingListSheet.dismiss()
        }

        //toast(App.getUserID(this))
        VollyApi.getDashBoardDetails(this)

        if (App.notifyMsg == null) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                VollyApi.updateDeviceToken(this, it)
            }
        }

        VollyApi.getNotification(this, "accepted_notifications", "DashboardActivity")


        Log.e("BAR_TOKEN", App.getUserToken(this))


        if (App.notifyMsg != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Request from patent")
            val message =
                "<b>Stating Address :</b> ${App.notifyMsg?.sourceLoc}<b><br>Destination Address :</b> ${App.notifyMsg?.desLoc}<br><b>Amount : </b>${App.notifyMsg?.amount}"
            builder.setMessage(Html.fromHtml(message))

            builder.setPositiveButton("Accept", DialogInterface.OnClickListener { dialog, which ->
                try {
                    val database = FirebaseDatabase.getInstance().reference.child("driver_data")
                    val updateInfo = HashMap<String, Any>()
                    updateInfo["online"] = false
                    val userId: Int = App.getUserID(this).toInt()
                    if (userId >= 0) {
                        database.child(App.getUserID(this)).updateChildren(updateInfo)
                    }
                } catch (e: java.lang.Exception) {
                    toast(e.message.toString())
                }

                VollyApi.updateBooking(this, App.notifyMsg?.bookingId!!, App.notifyMsg?.deviceId!!)
            })

            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, i ->

            })

            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }


        open_notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        navHeader = nav_view.getHeaderView(0)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {

                }
                R.id.menu_your_journey -> {

                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.menu_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                }
                R.id.document_upload -> {
                    startActivity(Intent(this, DocumentActivity::class.java))
                }
                R.id.schedule_booking_list -> {
                    VollyApi.getScheduleBooking(this)
                }
                R.id.menu_rej_notification -> {
                    startActivity(Intent(this, RejectedNotificationActivity::class.java))
                }
            }
            true
        }

        dashboard_logout.setOnClickListener {
            VollyApi.updateDeviceToken(this, "")
        }

        //Check switch
        val database = FirebaseDatabase.getInstance().reference.child("driver_data")
        database.child(App.getUserID(this)).get().addOnSuccessListener {
            if (it.exists()) {
                //toast(it.child("online").value.toString())
                online_switch.isChecked = it.child("online").value.toString() == "true"
            }
        }

        online_switch.setOnClickListener {
            val checkSwitch = online_switch.isChecked
            try {
                val updateInfo = HashMap<String, Any>()
                updateInfo["online"] = checkSwitch
                val userId: Int = App.getUserID(this).toInt()
                if (userId >= 0) {
                    database.child(App.getUserID(this)).updateChildren(updateInfo)
                }
            } catch (e: Exception) {
                toast(e.message.toString())
            }
        }


    }

    override fun onResume() {
        super.onResume()
        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        Log.e("DRIVER_TOKEN", App.getUserToken(this))
        apiCallViewModel.profileInfo(this)

        if (App.isLocationEnabled(this)) {


        } else {
            displayLocationSettingsRequest(this)
        }
    }

    fun logout() {

        try {
            val database = FirebaseDatabase.getInstance().reference.child("driver_data")
            val updateInfo = HashMap<String, Any>()
            updateInfo["online"] = false
            val userId: Int = App.getUserID(this).toInt()
            if (userId >= 0) {
                database.child(App.getUserID(this)).updateChildren(updateInfo)
                    .addOnSuccessListener {
                        val sp = applicationContext.getSharedPreferences("user_info", MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.clear()
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }.addOnFailureListener {
                        toast("DB Update failed.")
                    }
            }
        } catch (e: java.lang.Exception) {
            toast(e.message.toString())
        }


    }

    fun getDashboardRes(dashboard: GetDashboard) {
        total_earning.text = dashboard.total_earning
        total_journey.text = dashboard.total_journey
        avg_rating.text = dashboard.avg_rating
    }

    private fun observeViewModel() {
        apiCallViewModel.profileVar.observe(this, Observer {
            if (!it.error) {
                val sp = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString(App.preferenceUserId, it.data.id)
                editor.putString(App.preferenceUserPhone, it.data.phone)
                editor.putString(App.preferenceUserEmail, it.data.email)
                editor.putString(App.preferenceUserName, it.data.name)
                editor.commit()

                navHeader.nav_username.text = it.data.name
                navHeader.nav_phone.text = it.data.phone
                navHeader.nav_image.text = it.data.name.take(1)

                //Update data into firebase
                val gpsTracker = GpsTracker(this)
                val updateInfo = HashMap<String, Any>()
                updateInfo["name"] = it.data.name
                updateInfo["lat"] = gpsTracker.latitude.toString()
                updateInfo["lon"] = gpsTracker.longitude.toString()
                updateInfo["phone"] = it.data.phone
                val currentTime = Calendar.getInstance().time;
                updateInfo["dateTime"] = currentTime.toString();
                database = FirebaseDatabase.getInstance().reference.child("driver_data")
                database.child(App.getUserID(this)).updateChildren(updateInfo)


            }
        })
    }

    fun setNotiList(notiList: ArrayList<NotificationApiModel>) {
        val notificationAdapter = NotificationAdapter(this, true)
        dashboard_list.adapter = notificationAdapter
        notificationAdapter.updateData(notiList)
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(
                        "LOCATON_TAG",
                        "All location settings are satisfied."
                    )
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "LOCATON_TAG",
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this, 1)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.i("LOCATON_TAG", "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                    "LOCATON_TAG",
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

    fun getScheduleBookingList(objList: java.util.ArrayList<ScheduleBookingModel>) {
        bookingListSheet.show()
        val successScheduleAdapter = ScheduleBookingAdapter(this)
        bookingListSheet.schedule_booking_list.adapter = successScheduleAdapter
        successScheduleAdapter.updateData(objList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (App.isLocationEnabled(this)) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        } else {
            displayLocationSettingsRequest(this)
        }

    }


    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


}