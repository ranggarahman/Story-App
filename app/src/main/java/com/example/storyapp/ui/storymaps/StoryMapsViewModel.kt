package com.example.storyapp.ui.storymaps

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.response.ListStoryItem
import com.example.storyapp.data.database.story.StoryEntity
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryMapsViewModel(private val storyRepository: StoryRepository): ViewModel() {

    private val _storyLocation = MutableLiveData<List<ListStoryItem>>()
    val storyLocation: LiveData<List<ListStoryItem>> = _storyLocation

    fun getStoryLocation(token: String) {
        try {
            viewModelScope.launch {
                _storyLocation.value = storyRepository.getStoryLocation(token)
                Log.d(TAG, "RETRIVEAL NOT ERROR : ${_storyLocation.value}")
            }
        }catch (e : Exception){
            Log.d(TAG, "ERROR RETRIEVAL")
        }
    }

    companion object{
        private const val TAG = "StoryMapsViewModel"
    }

}