package org.relaxindia.driver

import com.google.gson.annotations.SerializedName

data class NotificationApiModel(
    val bookingId: Int,
    val details: String,
    val created_at: String,
    val userName: String,
    val userPhone: String,
    val userEmail: String,
    val isReached: Int,
    val activity: String

)
