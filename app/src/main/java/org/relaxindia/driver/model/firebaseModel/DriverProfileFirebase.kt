package org.relaxindia.driver.model.firebaseModel

import com.google.gson.annotations.SerializedName


data class DriverProfileFirebase(
    val id: Int,
    val name: String,
    val phone: String,
    val lat: String,
    val lon: String,
    val isLocationActive: Boolean,
    val isOnline: Boolean,


    )