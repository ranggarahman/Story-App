package com.example.storyapp.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.storyapp.R
import com.example.storyapp.data.repository.AuthRepository.Companion.SHARED_PREFS_NAME
import com.example.storyapp.data.repository.AuthRepository.Companion.TOKEN_KEY
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.databinding.LoadingOverlayBinding
import com.example.storyapp.ui.auth.login.LoginFragment.Companion.LOGIN_RESULT_OK
import com.example.storyapp.ui.auth.login.LoginFragment.Companion.LOGIN_RESULT_ONGOING
import com.example.storyapp.ui.auth.register.RegisterFragment.Companion.REGISTER_RESULT_OK
import com.example.storyapp.ui.auth.register.RegisterFragment.Companion.REGISTER_RESULT_ONGOING
import com.example.storyapp.ui.auth.validator.ResultListener
import com.example.storyapp.ui.storylist.StoryListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), ResultListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var loading: LoadingOverlayBinding
    private lateinit var loadingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        loading = binding.loadingOverlay
        loadingText = binding.loadingOverlay.textviewOverlay

        val preferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(TOKEN_KEY, null)

        if (token != null) {
            Log.d(TAG, "Token : $token")
            val intent = Intent(this, StoryListActivity::class.java)
            intent.putExtra(EXTRA_TOKEN, token)
            startActivity(intent)
            finish()
        } else {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_login, R.id.navigation_register
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    override fun onResult(resultCode: Int) {
        setResult(resultCode)
        when(resultCode){
            LOGIN_RESULT_OK, REGISTER_RESULT_OK -> {
                loading.loadingOverlay.visibility = View.GONE
            }
            LOGIN_RESULT_ONGOING -> {
                loadingText.text = getString(R.string.action_sign_in_loading)
                loading.loadingOverlay.visibility = View.VISIBLE
            }
            REGISTER_RESULT_ONGOING -> {
                loadingText.text = getString(R.string.action_register_loading)
                loading.loadingOverlay.visibility = View.VISIBLE
            }
        }

    }

    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_TOKEN = "extra_token"
    }

}
