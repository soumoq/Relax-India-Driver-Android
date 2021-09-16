package org.relaxindia.driver.model.driverProfile

import com.google.gson.annotations.SerializedName


data class ProfileInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("secondary_phone") val secondary_phone: String,
    @SerializedName("device_token") val device_token: String
)