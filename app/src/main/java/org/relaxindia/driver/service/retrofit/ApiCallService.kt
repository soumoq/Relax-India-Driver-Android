package org.relaxindia.driver.service.retrofit


import org.relaxindia.driver.model.GlobalResponse
import org.relaxindia.driver.model.driverProfile.ProfileRes
import org.relaxindia.driver.util.App
import org.relaxindia.driver.model.otp.UserToken
import retrofit2.Call
import retrofit2.http.*

interface ApiCallService {

    @FormUrlEncoded
    @POST(App.register)
    fun updateProfile(
        @Field("name")
        name: String,
        @Field("email")
        email: String,
        @Field("phone")
        phone: String,
        @Field("password")
        password: String,
        @Field("password_confirmation")
        password_confirmation: String,
    ): Call<GlobalResponse>

    @FormUrlEncoded
    @POST(App.verifyOtp)
    fun verifyOtp(
        @Field("phone")
        phone: String,
        @Field("otp")
        otp: String
    ): Call<UserToken>

    @FormUrlEncoded
    @POST(App.apiLogin)
    fun login(
        @Field("phone")
        phone: String,
        @Field("password")
        password: String
    ): Call<UserToken>

    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST(App.profile)
    fun profile(
        @Field("Authorization")
        authorization: String,
    ): Call<ProfileRes>


}