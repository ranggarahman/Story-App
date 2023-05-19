package com.example.storyapp.data

import android.util.Log
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.api.request.LoginRequest
import com.example.storyapp.data.api.request.RegisterRequest
import com.example.storyapp.data.api.response.LoginResponse
import com.example.storyapp.data.api.response.RegisterResponse
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.data.model.RegisteredUser
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class AuthDataSource {

    private val apiService = ApiConfig.getApiService()

    suspend fun login(email: String, password: String): Result<LoggedInUser> {
        val requestBody = LoginRequest(email, password)
        val response = apiService.login(requestBody)
        return handleLoginResponse(response)
    }


    private fun handleLoginResponse(response: Response<LoginResponse>): Result<LoggedInUser> {
        return if (response.isSuccessful){
            val loggedInUser = LoggedInUser(
                response.body()?.loginResult?.userId,
                response.body()?.loginResult?.name,
                response.body()?.loginResult?.token
                )

            Log.d(TAG, "onResponse LOGIN Success in $TAG, ${response.body()?.message}")
            Result.Success(loggedInUser)
        } else {
            val errorMessage = response.errorBody()?.string()?.let {errorBody ->
                try {
                    JSONObject(errorBody).getString("message")
                } catch (e: JSONException){
                    "Unknown error"
                }
            } ?: "Unknown error"

            Log.e(TAG, "onResponse Error in $TAG ${response.code()} $errorMessage")

            Result.Error("Registration Failed :  $errorMessage")
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisteredUser> {
        val requestBody = RegisterRequest(name, email, password)
        val response = apiService.register(requestBody)
        return handleRegisterResponse(response)
    }

    private fun handleRegisterResponse(response: Response<RegisterResponse>): Result<RegisteredUser> {
        return if (response.isSuccessful) {
            val registeredUser = RegisteredUser(response.body()?.error, response.body()?.message)
            Log.d(TAG, "onResponse REGISTER Success in $TAG, ${response.body()?.message}")
            Result.Success(registeredUser)
        } else {
            val errorMessage = response.errorBody()?.string()?.let {errorBody ->
                try {
                    JSONObject(errorBody).getString("message")
                } catch (e: JSONException) {
                    "Unknown error"
                }
            } ?: "Unknown error"
            Log.e(TAG, "onResponse Error in $TAG ${response.code()} $errorMessage")

            Result.Error("Registration Failed :  $errorMessage")
        }
    }

    companion object{
        private const val TAG = "AuthDataSource"
    }
}