package com.example.storyapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.storyapp.data.AuthDataSource
import com.example.storyapp.data.Result
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.data.model.RegisteredUser

class AuthRepository(val dataSource: AuthDataSource, val context: Context) {

    private var sharedPrefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        sharedPrefs.edit().remove(TOKEN_KEY).apply()
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        Log.d(TAG, "Login Function in $TAG Called, Result is $result")

        if (result is Result.Success) {
            result.data.token?.let { setLoggedInUser(it) }
            user = LoggedInUser(result.data.userId, result.data.name, result.data.token)
        }

        return result
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisteredUser> {
        //Call data Source
        val result = dataSource.register(name, email, password)

        Log.d(TAG, "Register Function in $TAG Called, Result is $result")

        return result
    }

    private fun setLoggedInUser(token: String) {
        // Store the token in SharedPreferences
        with (sharedPrefs.edit()) {
            putString(TOKEN_KEY, token)
            apply()
        }
    }

    companion object{
        private const val TAG = "AuthRepository"
        const val SHARED_PREFS_NAME = "MySharedPrefs"
        const val TOKEN_KEY = "token"
    }
}