package com.example.storyapp.ui.storydetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.response.StoryDetailResponse
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyDetail = MutableLiveData<StoryDetailResponse?>()
    val storyDetail: LiveData<StoryDetailResponse?> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStoryDetail(id : String?, token: String){
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = storyRepository.getStoryDetail(id, token)
                _storyDetail.value = result
                Log.d(TAG, "Success Response : $result")
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error getting story Detail: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    companion object {
        private const val TAG = "StoryDetailViewModel"
    }
}