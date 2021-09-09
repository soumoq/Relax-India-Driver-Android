package org.relaxindia.driver.retrofit


import org.relaxindia.driver.model.GlobalResponse
import org.relaxindia.driver.util.App
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

}