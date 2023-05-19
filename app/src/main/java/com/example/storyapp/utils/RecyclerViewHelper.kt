package com.example.storyapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.storyapp.ui.storydetail.StoryDetailActivity
import com.example.storyapp.ui.storylist.adapter.StoryListAdapter

object RecyclerViewHelper {
        const val EXTRA_ID = "extra_id"

        fun setupRecyclerView(
            context: Context,
            adapter: StoryListAdapter
        ) {
            adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
                override fun onItemClicked(
                    id: String,
                    holder: StoryListAdapter.StoryListViewHolder
                ) {
                    Toast.makeText(context, "Item Clicked", Toast.LENGTH_SHORT).show()

                    val intent = Intent(context, StoryDetailActivity::class.java)
                    intent.putExtra(EXTRA_ID, id)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            Pair(holder.imgPhoto, "image_cardview"),
                            Pair(holder.tvName, "username_description_cardview"),
                        )

                    context.startActivity(intent, optionsCompat.toBundle())
                }
            })
        }
}