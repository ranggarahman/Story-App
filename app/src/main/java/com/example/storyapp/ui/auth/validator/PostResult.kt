package com.example.storyapp.ui.auth.validator

import com.example.storyapp.ui.storypost.PostedStoryUserView

data class PostResult(
    val successPost: PostedStoryUserView? = null,
    val failedPost: PostedStoryUserView? = null
)
