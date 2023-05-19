package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.api.response.ListStoryItem
import com.example.storyapp.data.api.response.StoryDetailResponse
import com.example.storyapp.data.database.StoryRemoteMediator
import com.example.storyapp.data.database.story.StoryDatabase
import com.example.storyapp.data.database.story.StoryEntity

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStoryList(location: Int?, token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getStoryLocation(token: String): List<ListStoryItem>? {
        val response = apiService.getStoryList(1, 20, 1, token)
        return if (response.isSuccessful){
            response.body()?.listStory as List<ListStoryItem>
        } else {
            null
        }
    }

    suspend fun getStoryDetail(id: String?, token: String) : StoryDetailResponse? {
        val response = apiService.getStoryDetail(id, token)
        return if (response.isSuccessful){
            Log.d(TAG, "DETAIL : ${response.body()}")
            response.body() as StoryDetailResponse
        } else {
            Log.e(TAG, "DETAIL : ${null}")
            null
        }
    }

    companion object{
        private const val TAG = "StoryRepository"
    }
}