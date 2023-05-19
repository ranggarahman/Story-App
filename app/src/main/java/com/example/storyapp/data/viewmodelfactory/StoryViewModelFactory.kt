package com.example.storyapp.data.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.di.Injection
import com.example.storyapp.ui.storydetail.StoryDetailViewModel
import com.example.storyapp.ui.storylist.StoryListViewModel
import com.example.storyapp.ui.storymaps.StoryMapsViewModel

class StoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)){
            return StoryListViewModel(Injection.provideRepository(context)) as T
        }

        if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)){
            return StoryDetailViewModel(Injection.provideRepository(context)) as T
        }

        if (modelClass.isAssignableFrom(StoryMapsViewModel::class.java)){
            return StoryMapsViewModel(Injection.provideRepository(context)) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}