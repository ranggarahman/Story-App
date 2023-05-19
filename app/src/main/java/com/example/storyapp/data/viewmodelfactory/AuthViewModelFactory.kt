package com.example.storyapp.data.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.AuthDataSource
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.ui.auth.login.LoginViewModel
import com.example.storyapp.ui.auth.register.RegisterViewModel

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                authRepository = AuthRepository(
                    dataSource = AuthDataSource(),
                    context = context
                )
            ) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(
                authRepository = AuthRepository(
                    dataSource = AuthDataSource(),
                    context = context
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}