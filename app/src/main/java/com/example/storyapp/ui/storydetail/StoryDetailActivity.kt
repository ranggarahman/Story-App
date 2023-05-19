package com.example.storyapp.ui.storydetail

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.api.response.StoryDetailResponse
import com.example.storyapp.data.repository.AuthRepository.Companion.SHARED_PREFS_NAME
import com.example.storyapp.data.repository.AuthRepository.Companion.TOKEN_KEY
import com.example.storyapp.data.viewmodelfactory.StoryViewModelFactory
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.utils.RecyclerViewHelper.EXTRA_ID

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private val storyDetailViewModel by viewModels<StoryDetailViewModel>{
        StoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(TOKEN_KEY, null)

        val storyId = intent.getStringExtra(EXTRA_ID)

        token?.let { storyDetailViewModel.getStoryDetail(storyId, "Bearer $it") }

        storyDetailViewModel.storyDetail.observe(this){
            setStoryDetails(it)
        }

        Log.d(TAG, "$token $storyId")
    }

    private fun setStoryDetails(storyDetail: StoryDetailResponse?) {
        binding.usernameTextview.text = storyDetail?.story?.name
        binding.descriptionTextview.text = storyDetail?.story?.description

        Glide.with(this@StoryDetailActivity)
            .load(storyDetail?.story?.photoUrl)
            .into(binding.storyImageView)
    }

    companion object{
        private const val TAG = "StoryDetailActivity"
    }
}