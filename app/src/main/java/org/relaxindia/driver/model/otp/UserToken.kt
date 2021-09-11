package org.relaxindia.driver.model.otp
import com.google.gson.annotations.SerializedName
import org.relaxindia.model.otp.UserTokenData

data class UserToken(
    @SerializedName("error") val error : Boolean,
    @SerializedName("data") val data : UserTokenData,
    @SerializedName("message") val message : String
)
