package com.example.storyapp.ui.storylist

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.storyapp.R
import com.example.storyapp.ui.auth.MainActivity.Companion.EXTRA_TOKEN
import com.example.storyapp.ui.storylist.adapter.StoryListAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class StoryListActivityTest{

    private lateinit var scenario: ActivityScenario<StoryListActivity>

    @Before
    fun setup(){
        val tokenextra = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWp4UHh2NlNiWVRsRWRia0kiLCJpYXQiOjE2ODE1NDExNjl9.f4c4WVwUedEj1pGAI2yNrnPTifN4KJghJLy-GKOA54Y"
        val intent = Intent(ApplicationProvider.getApplicationContext(), StoryListActivity::class.java)
        intent.putExtra(EXTRA_TOKEN, tokenextra)
        scenario = ActivityScenario.launch(intent)
    }

    @Test
    fun scrollToPosition_clickItem() {
        // Scroll to a specific position
        onView(withId(R.id.recyclerView)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollToPosition<StoryListAdapter.StoryListViewHolder>(10)
        )

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollToPosition<StoryListAdapter.StoryListViewHolder>(2)
        )

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<StoryListAdapter.StoryListViewHolder>(
                2,
                click()
            )
        )
    }
}