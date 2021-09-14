package org.relaxindia.driver.model.driverProfile

import com.google.gson.annotations.SerializedName



data class ProfileInfo (
	@SerializedName("name") val name : String,
	@SerializedName("email") val email : String,
	@SerializedName("phone") val phone : Int,
	@SerializedName("address") val address : String,
	@SerializedName("secondary_phone") val secondary_phone : String
)