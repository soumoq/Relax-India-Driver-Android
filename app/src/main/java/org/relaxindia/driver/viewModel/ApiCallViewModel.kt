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
import org.relaxindia.driver.retrofit.ApiCallService
import org.relaxindia.driver.retrofit.RestApiServiceBuilder
import org.relaxindia.driver.util.App
import org.relaxindia.model.otp.OtpData
import org.relaxindia.model.otp.OtpResponse


class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val register = MutableLiveData<GlobalResponse>()
    val verifyOtp = MutableLiveData<OtpResponse>()


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

        val response: Call<OtpResponse> =
            restApiService.verifyOtp(phone, otp)

        response.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(
                call: Call<OtpResponse>,
                response: Response<OtpResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    verifyOtp.value = response.body()
                    Log.e("$LOG-verifyOtpInfo-if", "Success")
                } else {
                    App.openDialog(context, "Error", "Something went wrong with otp.")
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-verifyOtpInfo-onFailure", "${t.message}")
            }
        })
    }


}