package com.example.storyapp.ui.auth.login

data class LoggedInUserView(
    val message: String,
    val token: String?
    //... other data fields that may be accessible to the UI
)
