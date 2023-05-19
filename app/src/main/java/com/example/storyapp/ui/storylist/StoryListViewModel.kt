package com.example.storyapp.ui.storylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.api.response.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository

class StoryListViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStoryList(location: Int?, token: String) : LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getStoryList(location, token).cachedIn(viewModelScope)
    }
    companion object {
        private const val TAG = "StoryListViewModel"
    }
}