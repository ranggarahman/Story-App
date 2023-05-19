package com.example.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.api.response.ListStoryItem

//Ignore Unused Class
//I'm not deleting this for future reference
class StoryPagingSource(private val apiService: ApiService,
                        private val location: Int?,
                        private val token: String ): PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoryList(position, params.loadSize, location, token)

            Log.d(TAG, "$params")

            responseData.takeIf { it.isSuccessful }?.body()?.let { body ->
                val storyList = body.listStory as List<ListStoryItem>
                Log.d(TAG, "LIST : $storyList")
                LoadResult.Page(
                    data = storyList,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (storyList.isEmpty()) null else position + 1
                )
            } ?: LoadResult.Error(Exception("Response not successful"))

        } catch (exception: Exception) {
            Log.e(TAG, "$exception")
            LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryPagingSource"
    }
}