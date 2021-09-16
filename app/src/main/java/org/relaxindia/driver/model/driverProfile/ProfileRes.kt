package org.relaxindia.driver.model.driverProfile

import com.google.gson.annotations.SerializedName



data class ProfileRes (
	@SerializedName("error") val error : Boolean,
	@SerializedName("data") val data : ProfileInfo,
	@SerializedName("message") val message : String
)