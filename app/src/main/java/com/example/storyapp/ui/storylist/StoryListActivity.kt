package com.example.storyapp.ui.storylist

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.AuthDataSource
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.data.viewmodelfactory.StoryViewModelFactory
import com.example.storyapp.databinding.ActivityStoryListBinding
import com.example.storyapp.ui.auth.MainActivity
import com.example.storyapp.ui.auth.MainActivity.Companion.EXTRA_TOKEN
import com.example.storyapp.ui.auth.login.LoginFragment.Companion.EXTRA_BUNDLE
import com.example.storyapp.ui.storylist.adapter.LoadingStateAdapter
import com.example.storyapp.ui.storylist.adapter.StoryListAdapter
import com.example.storyapp.ui.storymaps.StoryMapsActivity
import com.example.storyapp.ui.storypost.StoryPostActivity
import com.example.storyapp.utils.RecyclerViewHelper

class StoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryListBinding
    private lateinit var authRepository: AuthRepository

    private lateinit var token: String

    private val storyListViewModel by viewModels<StoryListViewModel>{
        StoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(AuthDataSource(), this)

        setupToken()
        setupRecyclerView(false)

        storyListViewModel.isLoading.observe(this){
            showLoading(it)
        }

        setupNewStory()
    }

    private fun setupToken() {
        val tokenFromLogin = intent.extras?.getString(EXTRA_BUNDLE)
        val tokenFromExtra = intent.getStringExtra(EXTRA_TOKEN)
        Log.d(TAG, "TOKEN FROM EXTRA: $tokenFromExtra")
        Log.d(TAG, "TOKEN FROM LOGIN : $tokenFromLogin")

        token = if (tokenFromExtra != null) "Bearer $tokenFromExtra" else "Bearer $tokenFromLogin"
    }


    private fun setupRecyclerView(isRefresh: Boolean) {
        val adapter = StoryListAdapter()
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        if(isRefresh){
            adapter.refresh()
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
        }

        Log.d(TAG, "OBSERVER CALLED")
        storyListViewModel.getStoryList(0, token).observe(this){
            Log.d(TAG, "ISREFRESH : $isRefresh")
            adapter.submitData(lifecycle, it)
            RecyclerViewHelper.setupRecyclerView(
                this@StoryListActivity,
                adapter
            )
        }
    }

    private val launcherIntentStoryPost = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        Log.d(TAG, "${it.resultCode}")
        if(it.resultCode == UPLOAD_STORY_RESULT){
            setupRecyclerView(true)
            Log.d(TAG, "SETUP CALLED AGAIN")
        }
    }

    private fun setupNewStory() {
        binding.fabPostStory.setOnClickListener {
            val intent = Intent(this@StoryListActivity, StoryPostActivity::class.java)
            launcherIntentStoryPost.launch(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.option_menu_storylist, menu)

        val logout = menu?.findItem(R.id.btn_logout)
        val maps = menu?.findItem(R.id.btn_storyMapsActivity)

        logout?.setOnMenuItemClickListener {
            finish()

            val intent = Intent(this@StoryListActivity, MainActivity::class.java)
            startActivity(intent)

            authRepository.logout()
            true
        }

        maps?.setOnMenuItemClickListener {
            val intent = Intent(this, StoryMapsActivity::class.java)
            startActivity(intent)

            true
        }

        return true
    }

    companion object{
        private const val TAG = "StoryListActivity"
        const val UPLOAD_STORY_RESULT = 225
    }
}