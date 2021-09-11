package org.relaxindia.model.otp
import com.google.gson.annotations.SerializedName

data class UserTokenData(
    @SerializedName("access_token") val access_token : String,

)
