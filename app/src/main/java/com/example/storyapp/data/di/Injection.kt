package com.example.storyapp.data.di

import android.content.Context
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.database.story.StoryDatabase
import com.example.storyapp.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}