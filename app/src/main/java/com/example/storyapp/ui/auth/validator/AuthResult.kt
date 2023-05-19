package com.example.storyapp.ui.auth.validator

import com.example.storyapp.ui.auth.login.LoggedInUserView
import com.example.storyapp.ui.auth.register.RegisteredUserView

data class AuthResult(
    val successLogin: LoggedInUserView? = null,
    val successRegister: RegisteredUserView? = null,
    val failedLogin: LoggedInUserView? = null,
    val failedRegister: RegisteredUserView? = null
)
