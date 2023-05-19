package com.example.storyapp.data.database.story

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.api.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryEntity>?)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

//    @Query("SELECT * FROM stories WHERE lat IS NOT NULL AND lon IS NOT NULL")
//    suspend fun getStoriesWithLocation(): List<StoryEntity>

    @Query("DELETE FROM stories")
    fun deleteAll()
}