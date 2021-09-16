package org.relaxindia.driver.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.relaxindia.driver.model.GlobalResponse
import org.relaxindia.driver.model.driverProfile.ProfileRes
import org.relaxindia.driver.service.retrofit.ApiCallService
import org.relaxindia.driver.service.retrofit.RestApiServiceBuilder
import org.relaxindia.driver.util.App
import org.relaxindia.driver.model.otp.UserToken


class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val register = MutableLiveData<GlobalResponse>()
    val verifyOtp = MutableLiveData<UserToken>()
    val login = MutableLiveData<UserToken>()
    val profileVar = MutableLiveData<ProfileRes>()


    lateinit var progressDialog: ProgressDialog

    private val restApiService = RestApiServiceBuilder().buildService(ApiCallService::class.java)

    fun registerInfo(
        context: Context,
        name: String,
        email: String,
        phone: String,
        password: String,
        cPassword: String
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait will send you a otp")
        progressDialog.show()

        val response: Call<GlobalResponse> =
            restApiService.updateProfile(name, email, phone, password, cPassword)
        response.enqueue(object : Callback<GlobalResponse> {
            override fun onResponse(
                call: Call<GlobalResponse>,
                response: Response<GlobalResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    register.value = response.body()
                    Log.e("$LOG-registerInfo-if", "Success")
                } else {
                    App.openDialog(context, "Error", "Email id or phone number already register.")
                }
            }

            override fun onFailure(call: Call<GlobalResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-registerInfo-onFailure", "${t.message}")
            }
        })


    }

    fun verifyOtpInfo(context: Context, phone: String, otp: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we verify your otp")
        progressDialog.show()

        val response: Call<UserToken> =
            restApiService.verifyOtp(phone, otp)

        response.enqueue(object : Callback<UserToken> {
            override fun onResponse(
                call: Call<UserToken>,
                response: Response<UserToken>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    verifyOtp.value = response.body()
                    Log.e("$LOG-verifyOtpInfo-if", "Success")
                } else {
                    App.openDialog(context, "Error", "Something went wrong with otp.")
                }
            }

            override fun onFailure(call: Call<UserToken>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-verifyOtpInfo-onFailure", "${t.message}")
            }
        })
    }

    fun loginInfo(context: Context, phone: String, password: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we verify your otp")
        progressDialog.show()

        val response: Call<UserToken> =
            restApiService.login(phone, password)

        response.enqueue(object : Callback<UserToken> {
            override fun onResponse(
                call: Call<UserToken>,
                response: Response<UserToken>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    login.value = response.body()
                    Log.e("$LOG-loginInfo-if", "Success")
                } else {
                    App.openDialog(
                        context,
                        "Error",
                        "Something went wrong with username or password."
                    )
                }
            }

            override fun onFailure(call: Call<UserToken>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-loginInfo-onFailure", "${t.message}")
            }
        })
    }

    fun profileInfo(context: Context, token: String = App.getUserToken(context)) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait, fetching profile info")
        progressDialog.show()

        val response: Call<ProfileRes> =
            restApiService.profile(token)

        response.enqueue(object : Callback<ProfileRes> {
            override fun onResponse(call: Call<ProfileRes>, response: Response<ProfileRes>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    profileVar.value = response.body()
                    Log.e("$LOG-profileInfo-if", "Success")
                } else {
                    App.openDialog(
                        context,
                        "Error",
                        "Something went wrong with fetching user info."
                    )
                }
            }

            override fun onFailure(call: Call<ProfileRes>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-profileInfo-onFailure", "${t.localizedMessage} ${call.isCanceled}")

            }

        })

    }


}