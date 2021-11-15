package org.relaxindia.driver.service.updateLocation

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log

import com.google.firebase.database.FirebaseDatabase

import org.relaxindia.driver.service.GpsTracker

import android.widget.Toast
import org.relaxindia.driver.util.App.getUserID
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


class LocUpdateService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show()
        val timer = Timer()
        val gpsTracker = GpsTracker(this@LocUpdateService)
        val hourlyTask: TimerTask = object : TimerTask() {
            override fun run() {
                //Update data into firebase
                try {
                    val updateInfo = HashMap<String, Any>()
                    updateInfo["lat"] = gpsTracker.latitude.toString()
                    updateInfo["lon"] = gpsTracker.longitude.toString()
                    val database = FirebaseDatabase.getInstance().reference.child("driver_data")
                    val userId: Int = getUserID(this@LocUpdateService).toInt()
                    if (userId >= 0) {
                        database.child(getUserID(this@LocUpdateService)).updateChildren(updateInfo)
                    }
                    Log.e("LATLONAA", gpsTracker.latitude.toString())
                } catch (e: Exception) {
                    Log.e("ERROR_SERVICE", e.message.toString())
                }
            }

        }
        timer.schedule(hourlyTask, 0L, 30000)
    }


    override fun onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show()
    }

    override fun onStart(intent: Intent?, startid: Int) {
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show()
    }

}