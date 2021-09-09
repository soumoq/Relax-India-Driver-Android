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


class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val register = MutableLiveData<GlobalResponse>()

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
                    //register.value = response.errorBody()
                    Log.e("$LOG-registerInfo-else", "Else: $name $email $phone $password $cPassword \t" + response.errorBody())
                }
            }

            override fun onFailure(call: Call<GlobalResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-registerInfo-onFailure", "${t.message}")
            }
        })


    }


}