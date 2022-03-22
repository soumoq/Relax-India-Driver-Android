package org.relaxindia.driver.model

import com.google.gson.annotations.SerializedName


data class Reached(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String
)